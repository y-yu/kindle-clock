package kindleclock.usecase

import kindleclock.domain.eff.KindleClockEitherEffect.KindleClockEither
import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject
import kindleclock.domain.interface.infra.api.awair.AwairApiClient
import kindleclock.domain.interface.infra.api.natureremo.NatureRemoApiClient
import kindleclock.domain.interface.infra.api.openweathermap.OpenWeatherMapApiClient
import kindleclock.domain.interface.infra.api.switchbot.SwitchBotApiClient
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.KindleClockColor
import kindleclock.domain.model.error.KindleClockError
import monix.eval.Task
import monix.execution.Scheduler
import org.atnos.eff.addon.monix.task.*
import org.atnos.eff.either.*
import org.atnos.eff.syntax.either.*
import org.atnos.eff.Fx
import org.atnos.eff.syntax.addon.monix.task.toTaskOps
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class GetKindleClockInfoUsecaseImpl @Inject() (
  awairApiClient: AwairApiClient,
  natureRemoApiClient: NatureRemoApiClient,
  openWeatherMapApiClient: OpenWeatherMapApiClient,
  switchBotApiClient: SwitchBotApiClient,
  clock: Clock
)(implicit
  ec: ExecutionContext
) extends GetKindleClockInfoUsecase {
  type R = Fx.fx2[Task, KindleClockEither]

  private val scheduler: Scheduler = Scheduler(ec)

  def execute: Future[ShowKindleImageUsecaseResult] = {
    val zonedDateTime = ZonedDateTime.now(clock.withZone(DefaultTimeZone.jst))
    val backgroundColor =
      if (zonedDateTime.getHour <= 6 || zonedDateTime.getHour >= 17)
        KindleClockColor.Black
      else
        KindleClockColor.White

    for {
      natureRemoInfo <- natureRemoApiClient.getRoomInfo
      openWeatherMapInfo <- openWeatherMapApiClient.getOpenWeatherMapInfo
      switchBotMeterInfo <- switchBotApiClient.getMeterInfo
      awairInfo <- awairApiClient
        .getRoomInfo[R]
        .runEither[KindleClockError]
        .runAsync
        .runToFuture(scheduler)
    } yield ShowKindleImageUsecaseResult(
      awairInfo,
      natureRemoInfo,
      openWeatherMapInfo,
      switchBotMeterInfo,
      backgroundColor
    )
  }
}
