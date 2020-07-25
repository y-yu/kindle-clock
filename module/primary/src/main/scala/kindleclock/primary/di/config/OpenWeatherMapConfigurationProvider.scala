package kindleclock.primary.di.config

import java.net.URI
import javax.inject.Inject
import javax.inject.Provider
import kindleclock.domain.model.config.api.OpenWeatherMapConfiguration
import play.api.Configuration

class OpenWeatherMapConfigurationProvider @Inject() (
  configuration: Configuration
) extends Provider[OpenWeatherMapConfiguration] {
  private lazy val config =
    OpenWeatherMapConfiguration(
      new URI(configuration.get[String]("open-weather-map.endpoint")),
      configuration.get[String]("open-weather-map.id"),
      configuration.get[String]("open-weather-map.app-id")
    )

  override def get(): OpenWeatherMapConfiguration = config
}
