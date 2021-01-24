package kindleclock.domain.model

sealed abstract class Color(
  val value: String
)

object Color {
  case object Black extends Color("black")

  case object White extends Color("white")
}
