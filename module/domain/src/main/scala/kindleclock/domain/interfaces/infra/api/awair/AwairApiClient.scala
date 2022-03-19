package kindleclock.domain.interfaces.infra.api.awair

import kindleclock.domain.eff.KindleClockEitherEffect._kindleClockEither
import kindleclock.domain.model.awair.AwairRoomInfo
import org.atnos.eff.Eff
import org.atnos.eff.addon.monix.task._task
import scala.concurrent.Future

trait AwairApiClient {
  def getRoomInfo[R: _task: _kindleClockEither]: Eff[R, AwairRoomInfo]
}
