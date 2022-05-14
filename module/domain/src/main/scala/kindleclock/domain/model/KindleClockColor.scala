package kindleclock.domain.model

sealed abstract class KindleClockColor(
  val value: String
)

object KindleClockColor {
  case object Black extends KindleClockColor("black")

  case object White extends KindleClockColor("white")
}
