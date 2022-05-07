package kindleclock.domain.model.natureremo

import kindleclock.domain.model.ElectricEnergy
import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature

case class NatureRemoRoomInfo(
  temperature: Temperature,
  humidity: Humidity,
  electricEnergy: ElectricEnergy
)
