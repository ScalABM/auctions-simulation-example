import akka.actor.{Actor, ActorRef, PoisonPill, Terminated}


class Reaper(auctionService: ActorRef, settlementService: ActorRef) extends Actor {

  context.watch(auctionService); context.watch(settlementService)

  def receive: Receive = {
    case Terminated(actorRef) if actorRef == auctionService => settlementService ! PoisonPill
    case Terminated(actorRef) if actorRef == settlementService => context.system.terminate()
  }

}
