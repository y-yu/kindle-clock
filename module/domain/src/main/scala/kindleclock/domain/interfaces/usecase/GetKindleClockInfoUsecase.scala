package kindleclock.domain.interfaces.usecase

import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import kindleclock.domain.model.Color
import kindleclock.domain.model.awair.AwairRoomInfo
import kindleclock.domain.model.error.KindleClockError
import kindleclock.domain.model.natureremo.NatureRemoRoomInfo
import kindleclock.domain.model.openweathermap.OpenWeatherMapInfo
import scala.concurrent.Future

trait GetKindleClockInfoUsecase {
  def execute: Future[ShowKindleImageUsecaseResult]
}

object GetKindleClockInfoUsecase {
  case class ShowKindleImageUsecaseResult(
    awairRoomInfo: Either[KindleClockError, AwairRoomInfo],
    natureRemoRoomInfo: NatureRemoRoomInfo,
    openWeatherMapInfo: OpenWeatherMapInfo,
    backgroundColor: Color
  )
}
