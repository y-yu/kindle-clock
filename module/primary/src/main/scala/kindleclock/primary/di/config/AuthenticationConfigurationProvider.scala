package kindleclock.primary.di.config

import jakarta.inject.Inject
import jakarta.inject.Provider
import kindleclock.domain.model.config.auth.AuthenticationConfiguration
import play.api.Configuration

class AuthenticationConfigurationProvider @Inject() (
  configuration: Configuration
) extends Provider[AuthenticationConfiguration] {
  private lazy val config =
    AuthenticationConfiguration(
      configuration.getOptional[String]("auth.token"),
      configuration.get[String]("auth.query-key-name")
    )

  override def get(): AuthenticationConfiguration = config
}
