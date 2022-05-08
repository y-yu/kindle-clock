package kindleclock.infra.cache.redis

import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kindleclock.domain.interface.infra.cache.CacheClient
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisConnectionException
import scalapb.GeneratedMessage
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking
import scala.concurrent.duration.Duration

class RedisCacheClientJedisImpl[A] @Inject() (
  jedis: Jedis
)(implicit
  ec: ExecutionContext,
  binaryFormat: BinaryFormat[A]
) extends CacheClient[A] {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val charset = StandardCharsets.UTF_8

  private def closeOnErrorByDefault[B](default: B): PartialFunction[Throwable, B] = {
    case e: JedisConnectionException =>
      logger.warn("Jedis error!", e)
      jedis.close()

      default
  }

  override def get(
    keyName: String
  ): Future[Option[A]] = (for {
    binary <- Future(blocking(jedis.get(keyName.getBytes(charset))))
    result <-
      if (Option(binary).isEmpty || binary.isEmpty) Future.successful(None)
      else Future.fromTry(binaryFormat.parseFrom(binary).map(Some.apply))
  } yield result).recover(closeOnErrorByDefault(None))

  override def save(
    keyName: String,
    a: A,
    expiration: Duration
  ): Future[Boolean] =
    Future(
      jedis.setex(
        keyName.getBytes(charset),
        expiration.toSeconds,
        binaryFormat.toBytes(a)
      ) == "OK"
    ).recover(closeOnErrorByDefault(false))

}
