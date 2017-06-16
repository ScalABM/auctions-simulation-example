import org.economicsl.auctions.{Contract, OrderLike}
import org.economicsl.core.Tradable


case class RejectedOrder(order: Contract with OrderLike[_ <: Tradable])
