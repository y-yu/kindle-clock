package kindleclock.domain.model.awair

import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature

case class AwairRoomInfo(
  score: Score,
  temperature: Temperature,
  humidity: Humidity,
  co2: Co2,
  voc: Voc,
  pm25: Pm25
)
