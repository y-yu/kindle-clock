package kindleclock.domain.model.config.api

import java.net.URI

case class OpenWeatherMapConfiguration(
  openWeatherMapEndPoint: URI,
  id: String,
  appId: String
)
