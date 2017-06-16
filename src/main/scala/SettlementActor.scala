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
import org.economicsl.auctions.{ClearResult, Fill}
import play.api.libs.json.Json

import scala.collection.mutable


/** SettlementActor acts as a buffer for the `ClearResults` and then write them to disk upon termination. */
class SettlementActor(path: String) extends Actor {

  def receive: Receive = {
    case ClearResult(fills, _) => fills.foreach(stream => buffer += stream.toList)
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    val bw = new BufferedWriter(new FileWriter(new File(path)))
    bw.write(Json.toJson(buffer).toString())
    bw.close()
  }

  private[this] val buffer = mutable.Buffer.empty[List[Fill]]

}
