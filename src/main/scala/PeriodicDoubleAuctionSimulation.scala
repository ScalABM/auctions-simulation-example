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
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.economicsl.auctions.singleunit.orders.Order
import org.economicsl.auctions.singleunit.pricing.WeightedAveragePricingPolicy
import org.economicsl.auctions.Tradable

import scala.concurrent.duration.FiniteDuration
import scala.util.Random


/** Simulate a generic periodic double auction.
  *
  * @author davidrpugh
  */
object PeriodicDoubleAuctionSimulation extends App with OrderGenerator {

  val config = ConfigFactory.load("simulation.conf")

  // start the actor system
  val actorSystem = ActorSystem("AuctionSystem", config)

  // configure the pricing policy
  val k = config.getDouble("simulation.pricing-policy.weight")
  val pricingPolicy: WeightedAveragePricingPolicy[AppleStock] = new WeightedAveragePricingPolicy[AppleStock](weight = k)

  // configure the clearing schedule
  val initialDelay = FiniteDuration(config.getLong("simulation.auction.clearing.initial-delay"), "seconds")
  val interval = FiniteDuration(config.getLong("simulation.auction.clearing.interval"), "seconds")
  val clearingSchedule = ClearingSchedule(initialDelay, interval)

  // configure the settlement service
  val settlementService = actorSystem.actorOf(Props(classOf[SettlementActor], "output.json"), "settlement")

  // configure the auction mechanism
  val auctionClass = classOf[PeriodicDoubleAuctionActor[AppleStock]]
  val auctionProps = Props(auctionClass, pricingPolicy, clearingSchedule, settlementService)
  actorSystem.actorOf(auctionProps, "auction")

  // probably want to push Security up into an esl-auctions Tradable hierarchy?
  trait Security extends Tradable
  case class AppleStock(tick: Long) extends Security

  // configure the random order flow
  val seed = config.getLong("simulation.order-flow.seed")
  val prng = new Random(seed)
  val number = config.getInt("simulation.order-flow.number-orders")
  val orderFlow: Stream[Order[AppleStock]] = randomOrders(number, AppleStock(1), prng)

}
