package kindleclock.infra.cache.redis

import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kindleclock.domain.interfaces.infra.cache.CacheClient
import kindleclock.infra.datamodel.awair.AwairDataModel
import redis.clients.jedis.BinaryJedis
import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class RedisCacheClientJedisImpl @Inject() (
  jedis: Jedis
)(implicit
  ec: ExecutionContext
) extends CacheClient[AwairDataModel] {
  private val charset = StandardCharsets.UTF_8

  override def get(
    keyName: String
  ): Future[Option[AwairDataModel]] =
    Future(
      Option(
        jedis
          .get(keyName.getBytes(charset))
      ).map(AwairDataModel.parseFrom)
    )

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
    )

}
