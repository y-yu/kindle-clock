package kindleclock.infra.cache.redis

import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kindleclock.domain.interfaces.infra.cache.CacheClient
import kindleclock.domain.model.config.api.AwairConfiguration
import kindleclock.infra.datamodel.awair.AwairDataModel
import redis.clients.jedis.BinaryJedis
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class RedisCacheClientJedisImpl @Inject() (
  binaryJedis: BinaryJedis,
  awairConfiguration: AwairConfiguration
)(implicit
  ec: ExecutionContext
) extends CacheClient[AwairDataModel] {
  private val charset = StandardCharsets.UTF_8

  override def get: Future[Option[AwairDataModel]] =
    Future(
      Option(
        binaryJedis
          .get(awairConfiguration.cacheKeyName.getBytes(charset))
      ).map(AwairDataModel.parseFrom)
    )

  override def save(a: AwairDataModel, expiration: Duration): Future[Boolean] =
    Future(
      binaryJedis.setex(
        awairConfiguration.cacheKeyName.getBytes(charset),
        awairConfiguration.cacheExpire.toSeconds.toInt,
        a.toByteArray
      ) == "OK"
    )

}
