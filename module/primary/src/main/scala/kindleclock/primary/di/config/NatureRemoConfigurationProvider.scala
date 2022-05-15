package kindleclock.primary.di.config

import java.net.URI
import javax.inject.Inject
import javax.inject.Provider
import kindleclock.domain.model.config.api.NatureRemoConfiguration
import play.api.Configuration

class NatureRemoConfigurationProvider @Inject(
  configuration: Configuration
) extends Provider[NatureRemoConfiguration] {
  private lazy val config =
    NatureRemoConfiguration(
      new URI(configuration.get[String]("nature-remo.endpoint")),
      configuration.get[String]("nature-remo.oauth.token")
    )

  override def get(): NatureRemoConfiguration = config
}
