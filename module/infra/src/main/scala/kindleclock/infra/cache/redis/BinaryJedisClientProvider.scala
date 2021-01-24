package kindleclock.infra.cache.redis

import javax.inject.Inject
import javax.inject.Provider
import kindleclock.domain.model.config.redis.RedisConfiguration
import redis.clients.jedis.BinaryJedis

class BinaryJedisClientProvider @Inject() (
  redisConfiguration: RedisConfiguration
) extends Provider[BinaryJedis] {
  lazy val binaryJedis: BinaryJedis = new BinaryJedis(
    redisConfiguration.host
  )

  override def get(): BinaryJedis =
    binaryJedis
}
