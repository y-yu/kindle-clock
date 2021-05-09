package kindleclock.primary.di.config

import java.net.URI
import javax.inject.Inject
import javax.inject.Provider
import kindleclock.domain.model.config.redis.RedisConfiguration
import play.api.Configuration

class RedisConfigurationProvider @Inject() (
  configuration: Configuration
) extends Provider[RedisConfiguration] {
  private lazy val config =
    RedisConfiguration(
      new URI(configuration.get[String]("redis.url"))
    )

  override def get(): RedisConfiguration = config
}
