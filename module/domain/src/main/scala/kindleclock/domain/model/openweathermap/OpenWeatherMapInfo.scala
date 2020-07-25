package kindleclock.domain.model.openweathermap

import java.time.ZonedDateTime
import kindleclock.domain.model.WeatherIcon

case class OpenWeatherMapInfo(
  weatherIcon: WeatherIcon,
  updatedAt: ZonedDateTime
)
