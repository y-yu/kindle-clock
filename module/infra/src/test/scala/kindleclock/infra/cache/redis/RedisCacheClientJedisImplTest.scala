package kindleclock.infra.cache.redis

import java.net.URI
import kindleclock.infra.datamodel.awair.AwairDataModel
import kindleclock.infra.test.TestDataModel
import kindleclock.infra.test.TestInfo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import redis.clients.jedis.Jedis
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*

class RedisCacheClientJedisImplTest extends AnyFlatSpec with Diagrams with BeforeAndAfterAll {
  val jedis = new Jedis(
    new URI("redis://localhost:6379")
  )

  trait SetUpWithDockerRedis extends DockerRedisConfiguration {
    val dummyKeyName = "key_name"

    val sut = new RedisCacheClientJedisImpl[TestDataModel](
      jedis
    )
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    jedis.flushAll()
  }

  "save" should "store the data successfully" in new SetUpWithDockerRedis {
    val data = TestDataModel.of(
      id = 55,
      name = "name",
      info = Seq(TestInfo.of(1.0), TestInfo.of(2.2))
    )

    val actual = Await.result(
      sut.save(
        dummyKeyName,
        data,
        30.seconds
      ),
      5.seconds
    )

    assert(actual)
  }

  it should "store and get the data successfully" in new SetUpWithDockerRedis {
    val data = TestDataModel.of(
      id = 55,
      name = "name",
      info = Seq(TestInfo.of(1.0), TestInfo.of(2.2))
    )

    val actual = Await.result(
      for {
        _ <- sut.save(
          dummyKeyName,
          data,
          30.seconds
        )
        actual <- sut.get(dummyKeyName)
      } yield actual,
      5.seconds
    )

    assert(actual == Some(data))
  }
}
