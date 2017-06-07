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

import akka.actor.Actor
import org.economicsl.auctions.Tradable
import org.economicsl.auctions.singleunit.{ClearResult, Fill}
import play.api.libs.json.{JsArray, Json}


class SettlementActor(path: String) extends Actor {

  import Implicits._

  def receive: Receive = {
    case ClearResult(fills, _) => fills.foreach(stream => bw.write(toJson(stream.toIndexedSeq).toString)) // todo: avoid type conversions!
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    bw.close()
  }

  private[this] val bw = new BufferedWriter(new FileWriter(new File(path), true))

  private[this] def toJson[T <: Tradable](fills: IndexedSeq[Fill[T]]): JsArray = {
    new JsArray(fills.map(fill => Json.toJson(fill)))
  }

}
