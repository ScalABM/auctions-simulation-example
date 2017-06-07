import org.economicsl.auctions.{Price, Quantity, Tradable}
import org.economicsl.auctions.singleunit.Fill
import org.economicsl.auctions.singleunit.orders.{AskOrder, BidOrder, Order}
import play.api.libs.json.{JsValue, Json, Writes}

/**
  * Created by pughdr on 6/7/2017.
  */
object Implicits {

  // converts Fill[T] to JSON...should these be part of esl-auctions?
  implicit lazy val tradableWrites: Writes[Tradable] = new Writes[Tradable] {
    def writes(o: Tradable): JsValue = Json.obj(
      "tick" -> o.tick
    )
  }
  implicit def priceWrites[T <: Tradable]: Writes[Price] = Json.writes[Price]
  implicit def quantityWrites[T <: Tradable]: Writes[Quantity] = Json.writes[Quantity]

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
  implicit def fillWrites[T <: Tradable]: Writes[Fill[T]] = Json.writes[Fill[T]]

}
