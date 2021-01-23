package kindleclock.domain.model.config.redis

case class RedisConfiguration(
  host: String,
  port: Int,
  password: Option[String],
  useSSL: Boolean
)
