package kindleclock.primary.di.config

import java.net.URI
import javax.inject.Inject
import javax.inject.Provider
import kindleclock.domain.model.config.api.SwitchBotConfiguration
import play.api.Configuration
import scala.concurrent.duration.*

class SwitchBotConfigurationProvider @Inject() (
  configuration: Configuration
) extends Provider[SwitchBotConfiguration] {
  private lazy val config = SwitchBotConfiguration(
    switchBotEndpoint = new URI(configuration.get[String]("switchbot.endpoint")),
    oauthToken = configuration.get[String]("switchbot.oauth.token"),
    cacheExpire = configuration.get[Int]("switchbot.cache.expire-seconds").seconds,
    intervalMinutes = configuration.get[Int]("switchbot.interval-minutes"),
    cacheKeyName = configuration.get[String]("switchbot.cache.key-name")
  )

  override def get(): SwitchBotConfiguration =
    config
}
