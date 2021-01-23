package kindleclock.infra.cache.redis

import akka.util.ByteString
import javax.inject.Inject
import kindleclock.infra.datamodel.awair.AwairDataModel
import kindleclock.domain.interfaces.infra.cache.CacheClient
import kindleclock.domain.model.config.api.AwairConfiguration
import redis.ByteStringFormatter
import redis.RedisClient
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class RedisCacheClientImpl @Inject() (
  redisClient: RedisClient,
  awairConfiguration: AwairConfiguration
) extends CacheClient[AwairDataModel] {
  import RedisCacheClientImpl._

  override def get: Future[Option[AwairDataModel]] =
    redisClient.get(
      awairConfiguration.cacheKeyName
    )

  override def save(a: AwairDataModel, expiration: Duration): Future[Boolean] =
    redisClient.set(
      key = awairConfiguration.cacheKeyName,
      value = a,
      exSeconds =
        if (expiration.isFinite)
          Some(expiration.toSeconds)
        else
          None
    )
}

object RedisCacheClientImpl {
  implicit val byteStringFormatter: ByteStringFormatter[AwairDataModel] =
    new ByteStringFormatter[AwairDataModel] {
      override def serialize(data: AwairDataModel): ByteString =
        ByteString(data.toByteArray)

      override def deserialize(bs: ByteString): AwairDataModel =
        AwairDataModel.parseFrom(bs.toArray)
    }
}
