package kindleclock.usecase

import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject
import kindleclock.domain.interface.infra.api.awair.AwairApiClient
import kindleclock.domain.interface.infra.api.natureremo.NatureRemoApiClient
import kindleclock.domain.interface.infra.api.openweathermap.OpenWeatherMapApiClient
import kindleclock.domain.interface.infra.api.switchbot.SwitchBotApiClient
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class GetKindleClockInfoUsecaseImpl @Inject() (
  awairApiClient: AwairApiClient,
  natureRemoApiClient: NatureRemoApiClient,
  openWeatherMapApiClient: OpenWeatherMapApiClient,
  switchBotApiClient: SwitchBotApiClient
)(implicit
  ec: ExecutionContext
) extends GetKindleClockInfoUsecase {
  def execute: Future[ShowKindleImageUsecaseResult] = {
    for {
      natureRemoInfo <- natureRemoApiClient.getRoomInfo
      openWeatherMapInfo <- openWeatherMapApiClient.getOpenWeatherMapInfo
      switchBotMeterInfo <- switchBotApiClient.getMeterInfo
      awairInfo <- awairApiClient.getRoomInfo()
    } yield ShowKindleImageUsecaseResult(
      awairInfo,
      natureRemoInfo,
      openWeatherMapInfo,
      switchBotMeterInfo
    )
  }
}
