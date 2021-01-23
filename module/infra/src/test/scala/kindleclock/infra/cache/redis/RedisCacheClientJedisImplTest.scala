package kindleclock.infra.cache.redis

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
    "localhost",
    6379,
    false
  )

  trait SetUpWithDockerRedis extends DockerRedisConfiguration {
    val sut = new RedisCacheClientJedisImpl(
      jedis,
      awairConfiguration
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
          data,
          30.seconds
        )
        actual <- sut.get
      } yield actual,
      5.seconds
    )

    assert(actual == Some(data))
  }
}
