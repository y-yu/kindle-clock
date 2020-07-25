package kindleclock.domain.model.config.api

import java.net.URI

case class NatureRemoConfiguration(
  natureRemoEndpoint: URI,
  oauthToken: String
)
