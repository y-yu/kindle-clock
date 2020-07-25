package kindleclock.domain.interfaces.infra.api.awair

import kindleclock.domain.model.awair.AwairRoomInfo
import scala.concurrent.Future

trait AwairApiClient {
  def getRoomInfo: Future[AwairRoomInfo]
}
