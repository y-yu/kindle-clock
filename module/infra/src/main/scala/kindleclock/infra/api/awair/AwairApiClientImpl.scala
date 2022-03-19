package kindleclock.infra.api.awair

import kindleclock.domain.eff.KindleClockEitherEffect._kindleClockEither
import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject
import kindleclock.infra.datamodel.awair.AwairDataModel
import kindleclock.domain.interfaces.infra.api.awair.AwairApiClient
import kindleclock.domain.interfaces.infra.cache.CacheClient
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature
import kindleclock.domain.model.awair.AwairRoomInfo
import kindleclock.domain.model.awair.Co2
import kindleclock.domain.model.awair.Pm25
import kindleclock.domain.model.awair.Score
import kindleclock.domain.model.awair.Voc
import kindleclock.domain.model.config.api.AwairConfiguration
import kindleclock.domain.model.error.KindleClockError
import okhttp3.OkHttpClient
import okhttp3.Request
import monix.eval.Task
import org.atnos.eff.Eff
import org.atnos.eff.addon.monix.task.*
import org.atnos.eff.either.*
import org.atnos.eff.addon.monix.task._task
import org.slf4j.LoggerFactory
import play.api.libs.json.Reads
import scala.concurrent.Future
import play.api.libs.json.*
import scala.concurrent.ExecutionContext
import scala.concurrent.blocking

class AwairApiClientImpl @Inject() (
  awairConfiguration: AwairConfiguration,
  okHttpClient: OkHttpClient,
  cacheClient: CacheClient[AwairDataModel],
  clock: Clock
)(implicit
  executionContext: ExecutionContext
) extends AwairApiClient {

  import AwairApiClientImpl.*

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val getAwairRoomInfo: Request =
    new Request.Builder()
      .url(
        awairConfiguration.awairEndpoint
          .resolve(
            "/v1/users/self/devices/awair-r2/15817/air-data/latest?fahrenheit=false"
          )
          .toURL
      )
      .addHeader(
        "Authorization",
        Seq(
          "Bearer",
          awairConfiguration.oauthToken
        ).mkString(" ")
      )
      .build()

  def getRoomInfo[R: _task: _kindleClockEither]: Eff[R, AwairRoomInfo] =
    fromTask(Task.deferFutureAction { implicit ec =>
      getRoomInfo()
        .map(x => Right(x))
        .recover { case e: AwairApiError =>
          Left(e)
        }
    }).flatMap(
      fromEither[R, KindleClockError, AwairRoomInfo]
    )

  private[awair] def getRoomInfo(): Future[AwairRoomInfo] = {
    val now = ZonedDateTime.now(clock.withZone(DefaultTimeZone.jst))

    for {
      cachedDataOpt <- cacheClient.get(awairConfiguration.cacheKeyName)
      isTiming = now.getMinute % awairConfiguration.intervalMinutes == 0
      awairRoomInfo <- (isTiming, cachedDataOpt) match {
        case (true, _) | (false, None) =>
          for {
            roomInfo <- getFromApi
            _ <- cacheClient.save(
              awairConfiguration.cacheKeyName,
              AwairDataModel(
                roomInfo.score.value,
                roomInfo.temperature.value,
                roomInfo.humidity.value,
                roomInfo.co2.value,
                roomInfo.voc.value,
                roomInfo.pm25.value
              ),
              awairConfiguration.cacheExpire
            )
          } yield roomInfo
        case (false, Some(cachedData)) =>
          Future.successful(
            AwairRoomInfo(
              Score(cachedData.score),
              Temperature(cachedData.temperature),
              Humidity(cachedData.humidity),
              Co2(cachedData.co2),
              Voc(cachedData.voc),
              Pm25(cachedData.pm25)
            )
          )
      }
    } yield awairRoomInfo
  }

  private def getFromApi: Future[AwairRoomInfo] =
    for {
      awairRoomInfoString <- Future(
        blocking(
          okHttpClient
            .newCall(getAwairRoomInfo)
            .execute()
            .body()
            .string()
        )
      )
      result <-
        Json
          .fromJson[AwairRoomInfo](
            Json.parse(awairRoomInfoString)
          )
          .fold(
            { error =>
              val exception = AwairApiError(message = error.mkString(","))
              logger.error("Awair API failed!", exception)
              Future.failed(exception)
            },
            Future.successful
          )
    } yield result
}

object AwairApiClientImpl {
  case class SensorData(
    comp: String,
    value: JsNumber
  )

  case class AwairApiError(
    message: String = null,
    cause: Throwable = null
  ) extends KindleClockError(message, cause)

  implicit val sensorDataReads: Reads[SensorData] = Json.reads[SensorData]

  implicit val electricEnergyReads: Reads[AwairRoomInfo] =
    for {
      score <- ((__ \ "data")(0) \ "score").read[Int]
      sensors <- ((__ \ "data")(0) \ "sensors").read[List[SensorData]]
      result <- (for {
        temp <- sensors.find(_.comp == "temp")
        humid <- sensors.find(_.comp == "humid")
        co2 <- sensors.find(_.comp == "co2")
        voc <- sensors.find(_.comp == "voc")
        pm25 <- sensors.find(_.comp == "pm25")
      } yield {
        Reads.pure(
          AwairRoomInfo(
            Score(score),
            Temperature(temp.value.as[Double]),
            Humidity(humid.value.as[Double]),
            Co2(co2.value.as[Int]),
            Voc(voc.value.as[Int]),
            Pm25(pm25.value.as[Double])
          )
        )
      }).getOrElse(
        Reads.failed(s"${sensors.mkString(",")} doesn't have enough data.")
      )
    } yield result
}
