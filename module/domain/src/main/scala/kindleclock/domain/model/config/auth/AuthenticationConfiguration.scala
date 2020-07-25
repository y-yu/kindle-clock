package kindleclock.domain.model.config.auth

case class AuthenticationConfiguration(
  token: Option[String],
  queryKeyName: String
)
