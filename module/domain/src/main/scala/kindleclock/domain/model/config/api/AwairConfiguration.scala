package kindleclock.domain.model.config.api

import java.net.URI
import scala.concurrent.duration.Duration

case class AwairConfiguration(
  awairEndpoint: URI,
  oauthToken: String,
  cacheExpire: Duration,
  intervalMinutes: Int
)
