package kindleclock.domain.model.switchbot

sealed abstract class SwitchBotDeviceType(val value: String) extends Product with Serializable

object SwitchBotDeviceType {
  case object MeterPlus extends SwitchBotDeviceType("MeterPlus")

  case class Other(override val value: String) extends SwitchBotDeviceType(value)

  def valueOf(value: String): SwitchBotDeviceType = value match {
    case SwitchBotDeviceType.MeterPlus.value =>
      SwitchBotDeviceType.MeterPlus

    case str =>
      SwitchBotDeviceType.Other(str)
  }
}
