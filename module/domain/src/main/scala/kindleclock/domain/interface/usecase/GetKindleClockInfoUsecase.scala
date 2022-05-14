package kindleclock.domain.interface.usecase

import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import kindleclock.domain.model.KindleClockColor
import kindleclock.domain.model.awair.AwairRoomInfo
import kindleclock.domain.model.error.KindleClockError
import kindleclock.domain.model.natureremo.NatureRemoRoomInfo
import kindleclock.domain.model.openweathermap.OpenWeatherMapInfo
import kindleclock.domain.model.switchbot.SwitchBotMeterInfo
import scala.concurrent.Future

trait GetKindleClockInfoUsecase {
  def execute: Future[ShowKindleImageUsecaseResult]
}

object GetKindleClockInfoUsecase {
  case class ShowKindleImageUsecaseResult(
    awairRoomInfo: Either[KindleClockError, AwairRoomInfo],
    natureRemoRoomInfo: NatureRemoRoomInfo,
    openWeatherMapInfo: OpenWeatherMapInfo,
    switchBotMeterInfo: Seq[SwitchBotMeterInfo],
    backgroundColor: KindleClockColor
  )
}
