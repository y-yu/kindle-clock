package kindleclock.domain.interfaces.infra.cache

import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait CacheClient[A] {
  def get: Future[Option[A]]

  def save(
    a: A,
    expiration: Duration
  ): Future[Boolean]
}
