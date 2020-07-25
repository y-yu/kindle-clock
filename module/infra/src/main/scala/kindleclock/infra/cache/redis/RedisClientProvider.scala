package kindleclock.infra.cache.redis

import akka.actor.ActorSystem
import com.google.inject.Provider
import javax.inject.Inject
import kindleclock.domain.model.config.redis.RedisConfiguration
import redis.RedisClient

class RedisClientProvider @Inject() (
  redisConfiguration: RedisConfiguration
)(implicit
  actorSystem: ActorSystem
) extends Provider[RedisClient] {
  private lazy val redisClient = RedisClient(
    redisConfiguration.host,
    redisConfiguration.port,
    redisConfiguration.password
  )

  override def get(): RedisClient =
    redisClient
}
