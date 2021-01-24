package kindleclock.usecase

import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject
import kindleclock.domain.interfaces.infra.api.awair.AwairApiClient
import kindleclock.domain.interfaces.infra.api.natureremo.NatureRemoApiClient
import kindleclock.domain.interfaces.infra.api.openweathermap.OpenWeatherMapApiClient
import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase
import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.Color
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class GetKindleClockInfoUsecaseImpl @Inject() (
  awairApiClient: AwairApiClient,
  natureRemoApiClient: NatureRemoApiClient,
  openWeatherMapApiClient: OpenWeatherMapApiClient,
  clock: Clock
)(implicit
  ec: ExecutionContext
) extends GetKindleClockInfoUsecase {

  def execute: Future[ShowKindleImageUsecaseResult] = {
    val zonedDateTime = ZonedDateTime.now(clock.withZone(DefaultTimeZone.jst))
    val backgroundColor =
      if (zonedDateTime.getHour <= 6 || zonedDateTime.getHour >= 17)
        Color.Black
      else
        Color.White

    for {
      natureRemoInfo <- natureRemoApiClient.getRoomInfo
      openWeatherMapInfo <- openWeatherMapApiClient.getOpenWeatherMapInfo
      awairInfo <- awairApiClient.getRoomInfo
    } yield ShowKindleImageUsecaseResult(
      awairInfo,
      natureRemoInfo,
      openWeatherMapInfo,
      backgroundColor
    )
  }
}
