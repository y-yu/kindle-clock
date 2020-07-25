package kindleclock.domain.interfaces.infra.api.natureremo

import kindleclock.domain.model.natureremo.NatureRemoRoomInfo
import scala.concurrent.Future

trait NatureRemoApiClient {
  def getRoomInfo: Future[NatureRemoRoomInfo]

  def changeLightToWhite(): Future[Unit]
}
