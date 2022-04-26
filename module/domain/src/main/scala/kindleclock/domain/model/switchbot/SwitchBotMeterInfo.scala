package kindleclock.domain.model.switchbot

import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature

case class SwitchBotMeterInfo(
  deviceId: String,
  temperature: Temperature,
  humidity: Humidity
)
