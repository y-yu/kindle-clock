package kindleclock.infra.cache.redis

import javax.inject.Inject
import javax.inject.Provider
import kindleclock.domain.model.config.redis.RedisConfiguration
import redis.clients.jedis.Jedis

class JedisClientProvider @Inject() (
  redisConfiguration: RedisConfiguration
) extends Provider[Jedis] {
  lazy val jedis: Jedis = new Jedis(
    redisConfiguration.host,
    redisConfiguration.timeout.toMillis.toInt
  )

  override def get(): Jedis =
    jedis
}
