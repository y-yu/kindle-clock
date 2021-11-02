package kindleclock.primary.controller

import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import javax.inject.Inject
import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase
import kindleclock.primary.action.AuthenticatedActionBuilder
import kindleclock.primary.presenter.KindleClockPresenter
import play.api.Logging
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ShowKindleClockController @Inject() (
  kindleClockPresenter: KindleClockPresenter,
  getKindleClockInfoUsecase: GetKindleClockInfoUsecase,
  cc: ControllerComponents,
  authenticatedActionBuilder: AuthenticatedActionBuilder
)(implicit
  ec: ExecutionContext
) extends AbstractController(cc)
  with Logging {
  def show: Action[AnyContent] =
    authenticatedActionBuilder.action.async { _ =>
      (for {
        roomInfo <- getKindleClockInfoUsecase.execute
        result <- kindleClockPresenter.result(roomInfo)
      } yield result).recoverWith { case e: Throwable =>
        logger.error(e.getMessage, e)

        Future.failed(e)
      }
    }
}
