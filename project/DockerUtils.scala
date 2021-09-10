import sbt._

import Keys._
import sbt.taskKey
import sbt.util.Logger
import sys.process._

object DockerUtils {
  private val waitingTime = 2000 // milli seconds

  val runRedis = taskKey[Unit]("Run Redis in local Docker")

  val runRedisSetting = Seq(
    runRedis := {
      runLocalRedis(streams.value.log)
    }
  )

  private def runLocalRedis(
    log: Logger
  ): Unit = {
    val command = List(
      "docker-compose",
      "up",
      "--detach"
    ).mkString(" ")

    try {
      command.lineStream(log)

      log.info("Launch the Redis in Docker!")
      log.info(s"Wait ${waitingTime / 1000} seconds until Redis is up.")
      Thread.sleep(waitingTime)
    } catch {
      case _: Throwable =>
        sys.error("Fail to run Redis")
    }
  }

}
