package kindleclock.domain.interfaces.infra.cache

import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait CacheClient[A] {
  def get(
    keyName: String
  ): Future[Option[A]]

  def save(
    keyName: String,
    a: A,
    expiration: Duration
  ): Future[Boolean]
}
