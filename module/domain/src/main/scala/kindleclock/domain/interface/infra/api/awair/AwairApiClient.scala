package kindleclock.domain.interface.infra.api.awair

import kindleclock.domain.model.awair.AwairRoomInfo
import kindleclock.domain.model.error.KindleClockError

import scala.concurrent.Future

trait AwairApiClient {
  def getRoomInfo(): Future[Either[KindleClockError, AwairRoomInfo]]
}
