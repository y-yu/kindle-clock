package kindleclock.domain.model.config.redis

import java.net.URI
import scala.concurrent.duration.FiniteDuration

case class RedisConfiguration(
  host: URI,
  timeout: FiniteDuration
)
