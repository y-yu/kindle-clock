package kindleclock.usecase

import javax.inject.Inject
import kindleclock.domain.interfaces.infra.api.awair.AwairApiClient
import kindleclock.domain.interfaces.infra.api.natureremo.NatureRemoApiClient
import kindleclock.domain.interfaces.infra.api.openweathermap.OpenWeatherMapApiClient
import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase
import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class GetKindleClockInfoUsecaseImpl @Inject() (
  awairApiClient: AwairApiClient,
  natureRemoApiClient: NatureRemoApiClient,
  openWeatherMapApiClient: OpenWeatherMapApiClient
)(implicit
  ec: ExecutionContext
) extends GetKindleClockInfoUsecase {

  def execute: Future[ShowKindleImageUsecaseResult] =
    for {
      natureRemoInfo <- natureRemoApiClient.getRoomInfo
      openWeatherMapInfo <- openWeatherMapApiClient.getOpenWeatherMapInfo
      awairInfo <- awairApiClient.getRoomInfo
    } yield ShowKindleImageUsecaseResult(
      awairInfo,
      natureRemoInfo,
      openWeatherMapInfo
    )
}
