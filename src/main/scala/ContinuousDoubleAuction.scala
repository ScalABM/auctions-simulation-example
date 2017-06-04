/*
Copyright (c) 2017 KAPSARC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import java.io.{BufferedWriter, File, FileWriter}

import com.typesafe.config.ConfigFactory
import org.economicsl.auctions.{Price, Quantity, Tradable}
import org.economicsl.auctions.singleunit.{ClearResult, Fill}
import org.economicsl.auctions.singleunit.orders.{AskOrder, BidOrder, Order}
import org.economicsl.auctions.singleunit.pricing.WeightedAveragePricingPolicy
import org.economicsl.auctions.singleunit.twosided.SealedBidDoubleAuction
import play.api.libs.json._

import scala.util.Random


/** Simulate a generic continuous double auction.
  *
  * @author davidrpugh
  */
object ContinuousDoubleAuction extends App with OrderGenerator {

  val config = ConfigFactory.load("simulation.conf")

  // probably want to push Security up into an esl-auctions Tradable hierarchy?
  trait Security extends Tradable
  case class AppleStock(tick: Long) extends Security

  // configure the random order flow
  val seed = config.getLong("simulation.order-flow.seed")
  val prng = new Random(seed)
  val number = config.getInt("simulation.order-flow.number-orders")
  val orderFlow: Stream[Order[AppleStock]] = randomOrders(number, AppleStock(1), prng)

  // configure the pricing policy
  val k = config.getDouble("simulation.pricing-policy.weight")
  val pricingPolicy: WeightedAveragePricingPolicy[AppleStock] = new WeightedAveragePricingPolicy[AppleStock](weight = k)

  // configure the auction mechanism
  val doubleAuction: DoubleAuction[AppleStock] = SealedBidDoubleAuction.withDiscriminatoryPricing(pricingPolicy)

  val results = simulate(doubleAuction)(orderFlow)

  // at this point we would want to serialize the JSON results to a file...which we would then read into Pandas.
  val file = new File("./output.json")
  val bw = new BufferedWriter(new FileWriter(file))
  bw.write(toJson(results).toString)
  bw.close()

  // converts Fill[T] to JSON...should these be part of esl-auctions?
  implicit lazy val tradableWrites: Writes[Tradable] = new Writes[Tradable] {
    def writes(o: Tradable): JsValue = Json.obj(
      "tick" -> o.tick
    )
  }
  implicit def priceWrites[T <: Tradable]: OWrites[Price] = Json.writes[Price]
  implicit def quantityWrites[T <: Tradable]: OWrites[Quantity] = Json.writes[Quantity]

  implicit def orderWrites[T <: Tradable, O <: Order[T]]: Writes[O] = new Writes[O] {
    def writes(o: O): JsValue = Json.obj(
      "issuer" -> o.issuer,
      "limit" -> o.limit,
      "quantity" -> o.quantity,
      "tradable" -> o.tradable
    )
  }

  implicit def askOrderWrites[T <: Tradable]: Writes[AskOrder[T]] = orderWrites[T, AskOrder[T]]
  implicit def bidOrderWrites[T <: Tradable]: Writes[BidOrder[T]] = orderWrites[T, BidOrder[T]]
  implicit def fillWrites[T <: Tradable]: OWrites[Fill[T]] = Json.writes[Fill[T]]

  // type alias to simplify the type signatures of the simulate function...
  private[this] type DoubleAuction[T <: Tradable] = SealedBidDoubleAuction.DiscriminatoryPricingImpl[T]

  /** Converts a stream of clear results to JSON for further processing.
    *
    * @param results
    * @tparam T
    * @return
    */
  private[this] def toJson[T <: Tradable](results: Stream[ClearResult[T, DoubleAuction[T]]]): JsArray = {
    val fills: Stream[Fill[T]] = results.flatMap(result => result.fills).flatten
    new JsArray(fills.map(fill => Json.toJson(fill)).toIndexedSeq)
  }

  /** A lazy, tail-recursive simulation of a continuous double auction.
    *
    * @param auction
    * @param incoming
    * @tparam T
    * @return
    * @note Stream, as a lazy-list, is a last-in-first-out data structure. This means that if we want to process the
    *       results in the order in which they were generated we will need to process the stream in reverse.
    */
  private[this] def simulate[T <: Tradable](auction: DoubleAuction[T])(incoming: Stream[Order[T]]): Stream[ClearResult[T, DoubleAuction[T]]] = {
    @annotation.tailrec
    def loop(da: DoubleAuction[T], in: Stream[Order[T]], out: Stream[ClearResult[T, DoubleAuction[T]]]): Stream[ClearResult[T, DoubleAuction[T]]] = in match {
      case Stream.Empty => out
      case head #:: tail => head match {
        case order: AskOrder[T] =>
          val results = da.insert(order).clear  // continuous clearing!
          loop(results.residual, tail, results #:: out)
        case order: BidOrder[T] =>
          val results = da.insert(order).clear  // continuous clearing!
          loop(results.residual, tail, results #:: out)
      }
    }
    loop(auction, incoming, Stream.empty[ClearResult[T, DoubleAuction[T]]])
  }


}
