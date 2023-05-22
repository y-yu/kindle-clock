package kindleclock.primary.di.config

import java.net.URI
import jakarta.inject.Inject
import jakarta.inject.Provider
import kindleclock.domain.model.config.redis.RedisConfiguration
import play.api.Configuration
import scala.concurrent.duration.FiniteDuration

class RedisConfigurationProvider @Inject() (
  configuration: Configuration
) extends Provider[RedisConfiguration] {
  private lazy val config =
    RedisConfiguration(
      new URI(configuration.get[String]("redis.url")),
      configuration.get[FiniteDuration]("redis.timeout")
    )

  override def get(): RedisConfiguration = config
}
