package kindleclock.infra.cache.redis

import java.net.URI
import akka.actor.ActorSystem
import kindleclock.infra.datamodel.awair.AwairDataModel
import org.scalatest.BeforeAndAfterAll
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import redis.clients.jedis.Jedis
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class RedisCacheClientJedisImplTest extends AnyFlatSpec with Diagrams with BeforeAndAfterAll {
  implicit val actorSystem: ActorSystem = ActorSystem()

  val jedis = new Jedis(
    new URI("redis://localhost:6379")
  )

  trait SetUpWithDockerRedis extends DockerRedisConfiguration {
    val dummyKeyName = "key_name"

    val sut = new RedisCacheClientJedisImpl(
      jedis
    )
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    jedis.flushAll()
  }

  "save" should "store the data successfully" in new SetUpWithDockerRedis {
    val data = AwairDataModel(
      55,
      1.0,
      2.0,
      3,
      4,
      5.0
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
    val data = AwairDataModel(
      55,
      1.0,
      2.0,
      3,
      4,
      5.0
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
