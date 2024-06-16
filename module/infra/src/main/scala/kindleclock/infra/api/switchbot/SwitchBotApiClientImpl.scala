package kindleclock.infra.api.switchbot

import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject
import kindleclock.domain.interface.infra.api.switchbot.SwitchBotApiClient
import kindleclock.domain.interface.infra.cache.CacheClient
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature
import kindleclock.domain.model.config.api.SwitchBotConfiguration
import kindleclock.domain.model.switchbot.SwitchBotDeviceType
import kindleclock.domain.model.switchbot.SwitchBotMeterInfo
import kindleclock.infra.datamodel.switchbot.SwitchBotDevicesDataModel
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import play.api.libs.json.Reads
import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

class SwitchBotApiClientImpl @Inject() (
  okHttpClient: OkHttpClient,
  switchBotConfiguration: SwitchBotConfiguration,
  cacheClient: CacheClient[SwitchBotDevicesDataModel],
  clock: Clock
)(implicit
  executionContext: ExecutionContext
) extends SwitchBotApiClient {
  import SwitchBotApiClientImpl.*

  private val logger = LoggerFactory.getLogger(this.getClass)

  /** @see
    *   [[https://github.com/OpenWonderLabs/SwitchBotAPI?tab=readme-ov-file#authentication]]
    */
  private def requestWithAuthorization(path: String): Request = {
    val nonce = UUID.randomUUID.toString
    val time = s"${clock.instant().toEpochMilli}"
    val data = switchBotConfiguration.oauthToken + time + nonce

    val secretKeySpec =
      new SecretKeySpec(switchBotConfiguration.oauthSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKeySpec)
    val signature =
      new String(Base64.getEncoder.encode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8)

    new Request.Builder()
      .url(switchBotConfiguration.switchBotEndpoint.resolve(path).toURL)
      .addHeader("Authorization", switchBotConfiguration.oauthToken)
      .addHeader("sign", signature)
      .addHeader("nonce", nonce)
      .addHeader("t", time)
      .build()
  }

  private val getAllDevicesRequest: Request =
    requestWithAuthorization("/v1.1/devices")

  private def getMeterInfoRequest(deviceId: String): Request =
    requestWithAuthorization(s"/v1.0/devices/$deviceId/status")

  def getMeterInfo: Future[Seq[SwitchBotMeterInfo]] =
    for {
      allDevices <- getAllDevices
      meterIds = allDevices.collect { case (SwitchBotDeviceType.MeterPlus, deviceId) =>
        deviceId
      }
      meterJsonStrings <- Future.sequence(
        meterIds.toSeq.map(id =>
          Future(
            blocking(
              okHttpClient
                .newCall(getMeterInfoRequest(id))
                .execute()
                .body()
                .string()
            )
          )
        )
      )
      result <- Future.sequence(
        meterJsonStrings.map(meterJsonString =>
          Json
            .fromJson[SwitchBotMeterInfo](
              Json.parse(meterJsonString)
            )
            .fold(
              error =>
                Future.failed(
                  new IllegalArgumentException(
                    s"""JSON parse error!
                       |  error: ${error.mkString(",")}
                       |  response: $meterJsonString
                       |""".stripMargin
                  )
                ),
              Future.successful
            )
        )
      )
    } yield result

  /** @return
    *   Map(SwitchBotDeviceType -> DeviceId)
    */
  private def getAllDevices: Future[Map[SwitchBotDeviceType, String]] = {
    val now = ZonedDateTime.now(clock.withZone(DefaultTimeZone.jst))

    for {
      cachedDataOpt <- cacheClient.get(switchBotConfiguration.cacheKeyName)
      isTiming = now.getMinute % switchBotConfiguration.intervalMinutes == 0
      result <- (isTiming, cachedDataOpt) match {
        case (true, _) | (_, None) =>
          getAllDevicesFromAPI
        case (_, Some(cachedData)) =>
          Future.successful(
            cachedData.devices.map { device =>
              SwitchBotDeviceType.valueOf(device.deviceType) -> device.deviceId
            }.toMap
          )
      }
    } yield result
  }

  private def getAllDevicesFromAPI: Future[Map[SwitchBotDeviceType, String]] = for {
    allDevicesString <- Future(
      blocking(
        okHttpClient
          .newCall(getAllDevicesRequest)
          .execute()
          .body()
          .string()
      )
    )
    allDevices <-
      Json
        .fromJson[Map[SwitchBotDeviceType, String]](
          Json.parse(allDevicesString)
        )
        .fold(
          { error =>
            logger.warn(
              s"""JSON parse error!
                 |  error: ${error.mkString(",")}
                 |  response: $allDevicesString
                 |""".stripMargin
            )
            Future.successful(Map.empty[SwitchBotDeviceType, String])
          },
          Future.successful
        )
  } yield allDevices
}

object SwitchBotApiClientImpl {
  implicit val deviceListReads: Reads[Map[SwitchBotDeviceType, String]] = {
    (__ \ "body" \ "deviceList").read[JsArray].map { case JsArray(values) =>
      values
        .filter(v => (v \ "deviceType").isDefined)
        .map { v =>
          ((v \ "deviceType").as[SwitchBotDeviceType], (v \ "deviceId").as[String])
        }
        .toMap
    }
  }

  implicit val switchBotDeviceTypeReads: Reads[SwitchBotDeviceType] =
    __.read[String].map(SwitchBotDeviceType.valueOf)

  implicit val switchBotMeterInfoReads: Reads[SwitchBotMeterInfo] =
    ((__ \ "body" \ "deviceId").read[String] and
      (__ \ "body" \ "humidity").read[Int] and
      (__ \ "body" \ "temperature").read[Double]).tupled map { case (deviceId, humidity, temperature) =>
      SwitchBotMeterInfo(
        deviceId = deviceId,
        humidity = Humidity(humidity.toDouble),
        temperature = Temperature(temperature)
      )
    }
}
