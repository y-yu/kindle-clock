package kindleclock.infra.cache.redis

import java.net.URI
import kindleclock.domain.model.config.api.AwairConfiguration
import scala.concurrent.duration._

trait DockerRedisConfiguration {
  val awairConfiguration = AwairConfiguration(
    awairEndpoint = new URI("localhost"),
    oauthToken = "dummy",
    cacheExpire = 10.seconds,
    intervalMinutes = 999,
    cacheKeyName = "awair_cache"
  )
}
