package kindleclock.infra.cache.redis

import akka.util.ByteString
import javax.inject.Inject
import kindleclock.infra.datamodel.awair.AwairDataModel
import kindleclock.domain.interfaces.infra.cache.CacheClient
import redis.ByteStringFormatter
import redis.RedisClient
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class RedisCacheClientImpl @Inject() (
  redisClient: RedisClient
) extends CacheClient[AwairDataModel] {
  import RedisCacheClientImpl._

  override def get: Future[Option[AwairDataModel]] =
    redisClient.get(
      RedisCacheClientImpl.keyName
    )

  override def save(a: AwairDataModel, expiration: Duration): Future[Boolean] =
    redisClient.set(
      key = RedisCacheClientImpl.keyName,
      value = a,
      exSeconds =
        if (expiration.isFinite)
          Some(expiration.toSeconds)
        else
          None
    )
}

object RedisCacheClientImpl {
  private val keyName = "awair_cache"

  implicit val byteStringFormatter: ByteStringFormatter[AwairDataModel] =
    new ByteStringFormatter[AwairDataModel] {
      override def serialize(data: AwairDataModel): ByteString =
        ByteString(data.toByteArray)

      override def deserialize(bs: ByteString): AwairDataModel =
        AwairDataModel.parseFrom(bs.toArray)
    }
}
