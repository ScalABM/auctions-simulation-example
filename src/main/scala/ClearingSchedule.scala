import scala.concurrent.duration.FiniteDuration


case class ClearingSchedule(initialDelay: FiniteDuration, interval: FiniteDuration)
