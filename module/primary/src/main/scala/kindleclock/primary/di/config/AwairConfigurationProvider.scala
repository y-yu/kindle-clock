package kindleclock.primary.di.config

import java.net.URI
import jakarta.inject.Inject
import jakarta.inject.Provider
import kindleclock.domain.model.config.api.AwairConfiguration
import play.api.Configuration
import scala.concurrent.duration.*

class AwairConfigurationProvider @Inject() (
  configuration: Configuration
) extends Provider[AwairConfiguration] {
  private lazy val config = AwairConfiguration(
    new URI(configuration.get[String]("awair.endpoint")),
    configuration.get[String]("awair.oauth.token"),
    configuration.get[Int]("awair.cache.expire-seconds").seconds,
    configuration.get[Int]("awair.interval-minutes"),
    configuration.get[String]("awair.cache.key-name")
  )

  override def get(): AwairConfiguration = config
}
