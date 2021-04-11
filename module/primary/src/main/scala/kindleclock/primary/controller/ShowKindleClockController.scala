package kindleclock.primary.controller

import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import javax.inject.Inject
import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase
import kindleclock.primary.action.AuthenticatedActionBuilder
import kindleclock.primary.presenter.KindleClockPresenter
import scala.concurrent.ExecutionContext

class ShowKindleClockController @Inject() (
  kindleClockPresenter: KindleClockPresenter,
  getKindleClockInfoUsecase: GetKindleClockInfoUsecase,
  cc: ControllerComponents,
  authenticatedActionBuilder: AuthenticatedActionBuilder
)(implicit
  ec: ExecutionContext
) extends AbstractController(cc) {
  def show =
    authenticatedActionBuilder.action.async { _ =>
      (for {
        roomInfo <- getKindleClockInfoUsecase.execute
        result <- kindleClockPresenter.result(roomInfo)
      } yield result).recover { case e: Throwable =>
        InternalServerError(e.getMessage)
      }
    }
}
