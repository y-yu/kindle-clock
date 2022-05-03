package kindleclock.infra.cache.redis

import java.net.URI
import java.util.concurrent.Executors
import kindleclock.domain.model.config.redis.RedisConfiguration
import org.scalatest.BeforeAndAfterAll
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.*

class JedisClientProviderTest extends AnyFlatSpec with Diagrams with BeforeAndAfterAll {
  val jedisConfiguration = RedisConfiguration(
    new URI("redis://localhost:6379"),
    2.seconds
  )
  implicit val ec: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))

  val sut = new JedisClientProvider(
    jedisConfiguration
  )

  override def beforeAll(): Unit = {
    val client = sut.get()
    client.flushAll()
    super.beforeAll()
  }

  "get" should "return Jedis object successfully" in {
    val actual = sut.get()

    assert(actual.isConnected)
    assert(!actual.isBroken)
    assert(actual.ping() === "PONG")
  }

  it should "return Jedis object even thought the client has already been closed" in {
    sut.get().close()
    val actual = sut.get()

    assert(actual.isConnected)
    assert(!actual.isBroken)
    assert(actual.ping() === "PONG")
  }

  it should "be thread safe" in {
    val range = (1 to 100).toList

    val actual = Await.result(
      Future.sequence(
        range.map { _ =>
          Future {
            sut.get()
          }
        }
      ),
      2.seconds
    )

    assert(actual.length === range.length)
  }
}
