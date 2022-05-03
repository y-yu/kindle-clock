package kindleclock.infra.cache.redis

import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kindleclock.domain.interfaces.infra.cache.CacheClient
import kindleclock.infra.datamodel.awair.AwairDataModel
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisConnectionException
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class RedisCacheClientJedisImpl @Inject() (
  jedis: Jedis
)(implicit
  ec: ExecutionContext
) extends CacheClient[AwairDataModel] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val charset = StandardCharsets.UTF_8

  private def closeOnErrorByDefault[A](default: A): PartialFunction[Throwable, A] = {
    case e: JedisConnectionException =>
      logger.warn("Jedis error!", e)
      jedis.close()

      default
  }

  override def get(
    keyName: String
  ): Future[Option[AwairDataModel]] =
    Future(
      Option(
        jedis
          .get(keyName.getBytes(charset))
      ).map(AwairDataModel.parseFrom)
    ).recover(closeOnErrorByDefault(None))

  override def save(
    keyName: String,
    a: AwairDataModel,
    expiration: Duration
  ): Future[Boolean] =
    Future(
      jedis.setex(
        keyName.getBytes(charset),
        expiration.toSeconds,
        a.toByteArray
      ) == "OK"
    ).recover(closeOnErrorByDefault(false))

}
