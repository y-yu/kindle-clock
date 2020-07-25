package kindleclock.infra.di

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import kindleclock.infra.api.awair.AwairApiClientImpl
import kindleclock.infra.api.natureremo.NatureRemoApiClientImpl
import kindleclock.infra.api.openweathermap.OpenWeatherMapApiClientImpl
import kindleclock.infra.cache.redis.RedisCacheClientImpl
import kindleclock.infra.cache.redis.RedisClientProvider
import kindleclock.infra.datamodel.awair.AwairDataModel
import kindleclock.domain.interfaces.infra.api.awair.AwairApiClient
import kindleclock.domain.interfaces.infra.api.natureremo.NatureRemoApiClient
import kindleclock.domain.interfaces.infra.api.openweathermap.OpenWeatherMapApiClient
import kindleclock.domain.interfaces.infra.cache.CacheClient
import redis.RedisClient

class InfraModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[RedisClient]).toProvider(classOf[RedisClientProvider])
    bind(new TypeLiteral[CacheClient[AwairDataModel]]() {}).to(classOf[RedisCacheClientImpl])
    bind(classOf[AwairApiClient]).to(classOf[AwairApiClientImpl])
    bind(classOf[NatureRemoApiClient]).to(classOf[NatureRemoApiClientImpl])
    bind(classOf[OpenWeatherMapApiClient]).to(classOf[OpenWeatherMapApiClientImpl])
  }
}
