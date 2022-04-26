package kindleclock.domain.model.config.api

import java.net.URI
import scala.concurrent.duration.Duration

case class SwitchBotConfiguration(
  switchBotEndpoint: URI,
  oauthToken: String,
  cacheExpire: Duration,
  intervalMinutes: Int,
  cacheKeyName: String
)
