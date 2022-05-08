package kindleclock.infra.api.openweathermap

import java.time.ZonedDateTime
import java.time.Instant
import javax.inject.Inject
import kindleclock.domain.interface.infra.api.openweathermap.OpenWeatherMapApiClient
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.WeatherIcon
import kindleclock.domain.model.config.api.OpenWeatherMapConfiguration
import kindleclock.domain.model.openweathermap.OpenWeatherMapInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import play.api.libs.json.Reads
import scala.concurrent.Future
import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import scala.concurrent.ExecutionContext
import scala.concurrent.blocking

class OpenWeatherMapApiClientImpl @Inject() (
  okHttpClient: OkHttpClient,
  openWeatherMapConfiguration: OpenWeatherMapConfiguration
)(implicit
  executionContext: ExecutionContext
) extends OpenWeatherMapApiClient {
  import OpenWeatherMapApiClientImpl.*

  private val getOpenWeatherMapInfoRequest: Request =
    new Request.Builder()
      .url(
        openWeatherMapConfiguration.openWeatherMapEndPoint
          .resolve(
            s"/data/2.5/weather?units=metric&id=${openWeatherMapConfiguration.id}&APPID=${openWeatherMapConfiguration.appId}"
          )
          .toURL
      )
      .build()

  def getOpenWeatherMapInfo: Future[OpenWeatherMapInfo] =
    for {
      responseString <- Future(
        blocking(
          okHttpClient
            .newCall(getOpenWeatherMapInfoRequest)
            .execute()
            .body()
            .string()
        )
      )
      openWeatherMapInfo <-
        Json
          .fromJson[OpenWeatherMapInfo](
            Json.parse(responseString)
          )
          .fold(
            error => Future.failed(new IllegalArgumentException(error.mkString(","))),
            Future.successful
          )
    } yield openWeatherMapInfo
}

object OpenWeatherMapApiClientImpl {
  import WeatherIcon._

  // See https://openweathermap.org/weather-conditions
  def convertToIcon(iconString: String): JsResult[WeatherIcon] =
    iconString match {
      case "01d" | "01n" =>
        JsSuccess(Sunny)
      case "02d" | "02n" =>
        JsSuccess(SunnyWithCloud)
      case "03d" | "03n" =>
        JsSuccess(CloudyWithSun)
      case "04d" | "04n" =>
        JsSuccess(Cloudy)
      case "09d" | "09n" =>
        JsSuccess(RainyWithSun)
      case "10d" | "10n" =>
        JsSuccess(Rainy)
      case "11d" | "11n" =>
        JsSuccess(Thunder)
      case "13d" | "13n" =>
        JsSuccess(Snowing)
      case "50d" | "50n" =>
        JsSuccess(Mist)
      case _ =>
        JsError(s"Fail to convert $iconString to the icon data.")
    }

  implicit val openWeatherMapInfoReads: Reads[OpenWeatherMapInfo] =
    (((__ \ "weather")(0) \ "icon").read[String] and
      (__ \ "dt").read[Long]).tupled.flatMap { case (icon, dt) =>
      Reads[OpenWeatherMapInfo] { _ =>
        convertToIcon(icon).map(weatherIcon =>
          OpenWeatherMapInfo(
            weatherIcon,
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(dt), DefaultTimeZone.jst)
          )
        )
      }
    }
}
