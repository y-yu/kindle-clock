package kindleclock.primary.di

import java.time.Clock
import com.google.inject.AbstractModule
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.config.api.AwairConfiguration
import kindleclock.domain.model.config.api.NatureRemoConfiguration
import kindleclock.domain.model.config.api.OpenWeatherMapConfiguration
import kindleclock.domain.model.config.api.SwitchBotConfiguration
import kindleclock.domain.model.config.auth.AuthenticationConfiguration
import kindleclock.domain.model.config.redis.RedisConfiguration
import kindleclock.primary.di.config.AuthenticationConfigurationProvider
import kindleclock.primary.di.config.AwairConfigurationProvider
import kindleclock.primary.di.config.NatureRemoConfigurationProvider
import kindleclock.primary.di.config.OpenWeatherMapConfigurationProvider
import kindleclock.primary.di.config.RedisConfigurationProvider
import kindleclock.primary.di.config.SwitchBotConfigurationProvider

class PrimaryModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Clock]).toInstance(Clock.system(DefaultTimeZone.jst))
    bind(classOf[RedisConfiguration]).toProvider(classOf[RedisConfigurationProvider])
    bind(classOf[NatureRemoConfiguration]).toProvider(classOf[NatureRemoConfigurationProvider])
    bind(classOf[AwairConfiguration]).toProvider(classOf[AwairConfigurationProvider])
    bind(classOf[OpenWeatherMapConfiguration]).toProvider(classOf[OpenWeatherMapConfigurationProvider])
    bind(classOf[AuthenticationConfiguration]).toProvider(classOf[AuthenticationConfigurationProvider])
    bind(classOf[SwitchBotConfiguration]).toProvider(classOf[SwitchBotConfigurationProvider])
  }
}
