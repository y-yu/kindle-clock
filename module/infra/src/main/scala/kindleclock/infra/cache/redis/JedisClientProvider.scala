package kindleclock.infra.cache.redis

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kindleclock.domain.model.config.redis.RedisConfiguration
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import scala.concurrent.blocking

@Singleton
class JedisClientProvider @Inject(
  redisConfiguration: RedisConfiguration
) extends Provider[Jedis] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val jedisPool = new JedisPool(
    redisConfiguration.host,
    redisConfiguration.timeout.toMillis.toInt
  )

  private var jedis: Jedis = jedisPool.getResource
  private val lock = new Object

  override def get(): Jedis = blocking {
    lock.synchronized {
      if (jedis.isBroken || !jedis.isConnected) {
        jedis = jedisPool.getResource
        logger.info(s"Jedis connection is ${jedis.isConnected}")
        jedis
      } else {
        jedis
      }
    }
  }

}
