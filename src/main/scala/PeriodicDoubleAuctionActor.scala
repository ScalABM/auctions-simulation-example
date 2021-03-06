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
import akka.actor.{Actor, ActorRef}
import org.economicsl.auctions.singleunit.orders.{AskOrder, BidOrder}
import org.economicsl.auctions.singleunit.pricing.PricingPolicy
import org.economicsl.auctions.singleunit.twosided.SealedBidDoubleAuction
import org.economicsl.core.{Currency, Tradable}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}


class PeriodicDoubleAuctionActor[T <: Tradable](pricingPolicy: PricingPolicy[T],
                                                tickSize: Currency,
                                                clearings: ClearingSchedule,
                                                settlementService: ActorRef)
  extends Actor {

  schedule(clearings.initialDelay, clearings.interval)(context.dispatcher)

  def receive: Receive = {
    case order: AskOrder[T] => auction.insert(order) match {
      case Success(updated) => auction = updated  // todo consider responding with AcceptedOrder?
      case Failure(_) => sender() ! RejectedOrder(order)
    }
    case order: BidOrder[T] => auction.insert(order) match {
      case Success(updated) => auction = updated  // todo consider responding with AcceptedOrder?
      case Failure(_) => sender() ! RejectedOrder(order)
    }
    case ClearRequest =>
      val results = auction.clear
      settlementService ! results
      auction = results.residual
  }

  private[this] var auction = SealedBidDoubleAuction.withUniformPricing(pricingPolicy, tickSize)

  // clearing schedule should be a plugin...
  private[this] def schedule(initialDelay: FiniteDuration, interval: FiniteDuration)(implicit ec: ExecutionContext): Unit = {
    context.system.scheduler.schedule(initialDelay, interval)(self ! ClearRequest)(ec)
  }

  case object ClearRequest

}
