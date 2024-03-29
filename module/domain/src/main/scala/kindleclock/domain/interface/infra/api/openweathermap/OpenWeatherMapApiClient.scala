package kindleclock.domain.interface.infra.api.openweathermap

import kindleclock.domain.model.openweathermap.OpenWeatherMapInfo
import scala.concurrent.Future

trait OpenWeatherMapApiClient {
  def getOpenWeatherMapInfo: Future[OpenWeatherMapInfo]
}
