package kindleclock.domain.interfaces.infra.api.switchbot

import kindleclock.domain.model.switchbot.SwitchBotMeterInfo
import scala.concurrent.Future

trait SwitchBotApiClient {
  def getMeterInfo: Future[Seq[SwitchBotMeterInfo]]
}
