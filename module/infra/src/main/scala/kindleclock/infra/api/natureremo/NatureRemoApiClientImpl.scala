package kindleclock.infra.api.natureremo

import javax.inject.Inject
import kindleclock.domain.interfaces.infra.api.natureremo.NatureRemoApiClient
import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature
import kindleclock.domain.model.config.api.NatureRemoConfiguration
import kindleclock.domain.model.natureremo.ElectricEnergy
import kindleclock.domain.model.natureremo.NatureRemoRoomInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

class NatureRemoApiClientImpl @Inject() (
  okHttpClient: OkHttpClient,
  natureRemoConfiguration: NatureRemoConfiguration
)(implicit
  executionContext: ExecutionContext
) extends NatureRemoApiClient {

  import NatureRemoApiClientImpl._

  private def requestWithAuthorization(path: String): Request =
    new Request.Builder()
      .url(natureRemoConfiguration.natureRemoEndpoint.resolve(path).toURL)
      .addHeader(
        "Authorization",
        Seq(
          "Bearer",
          natureRemoConfiguration.oauthToken
        ).mkString(" ")
      )
      .build()

  private val getRoomTemperatureAndHumidityRequest: Request =
    requestWithAuthorization("/1/devices")

  private val getElectricEnergyRequest: Request =
    requestWithAuthorization("/1/appliances")

  def getRoomInfo: Future[NatureRemoRoomInfo] =
    for {
      temperatureAndHumidityString <- Future(
        blocking(
          okHttpClient
            .newCall(getRoomTemperatureAndHumidityRequest)
            .execute()
            .body()
            .string()
        )
      )
      electricEnergyString <- Future(
        blocking(
          okHttpClient
            .newCall(getElectricEnergyRequest)
            .execute()
            .body()
            .string()
        )
      )
      result <- (for {
        temperatureAndHumidity <- Json.fromJson[(Temperature, Humidity)](
          Json.parse(temperatureAndHumidityString)
        )
        electricEnergy <- Json.fromJson[ElectricEnergy](Json.parse(electricEnergyString))
      } yield NatureRemoRoomInfo(
        temperatureAndHumidity._1,
        temperatureAndHumidity._2,
        electricEnergy
      ))
        .fold(
          error => Future.failed(new IllegalArgumentException(error.mkString(","))),
          Future.successful
        )
    } yield result

  def changeLightToWhite(): Future[Unit] = ???
}

object NatureRemoApiClientImpl {
  implicit val temperatureAndHumidityReads: Reads[(Temperature, Humidity)] =
    ((__(0) \ "newest_events" \ "te" \ "val").read[Double] and
      (__(0) \ "newest_events" \ "hu" \ "val").read[Double])((t, h) => (Temperature(t), Humidity(h)))

  /** @see [[https://developer.nature.global/jp/how-to-calculate-energy-data-from-smart-meter-values]]
    */
  val nowElectricEnergyNumber = 231

  case class ElectricEnergyData(
    name: String,
    epc: Int,
    `val`: String,
    updated_at: String
  )

  implicit val electricEnergyDataReads: Reads[ElectricEnergyData] = Json.reads[ElectricEnergyData]

  implicit val electricEnergyReads: Reads[ElectricEnergy] =
    for {
      smartMeterFilteredJson <- __.read[JsArray].map { case JsArray(values) =>
        JsArray(
          values.filter { json =>
            (json \ "smart_meter").isDefined
          }
        )
      }
      result <-
        (smartMeterFilteredJson(0) \ "smart_meter" \ "echonetlite_properties")
          .as[List[ElectricEnergyData]]
          .find(_.epc == nowElectricEnergyNumber)
          .map(i => Reads.pure(ElectricEnergy(i.`val`.toInt)))
          .getOrElse(
            Reads.failed(s"$nowElectricEnergyNumber is not found.")
          )
    } yield result
}
