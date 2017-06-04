import org.economicsl.auctions.Tradable
import org.economicsl.auctions.singleunit.ClearResult
import org.economicsl.auctions.singleunit.orders.{AskOrder, BidOrder, Order}
import org.economicsl.auctions.singleunit.twosided.SealedBidDoubleAuction


/** Simulate a generic continuous double auction.
  *
  * @author davidrpugh
  */
object ContinuousDoubleAuction extends App {

  // type alias to simplify the type signatures of the simulate function...
  type DoubleAuction[T <: Tradable] = SealedBidDoubleAuction.DiscriminatoryPricingImpl[T]

  // A lazy, tail-recursive implementation of a continuous double auction!
  def simulate[T <: Tradable](auction: DoubleAuction[T])(incoming: Stream[Order[T]]): Stream[ClearResult[T, DoubleAuction[T]]] = {
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
