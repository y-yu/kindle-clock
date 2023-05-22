package kindleclock.primary.controller

import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import jakarta.inject.Inject
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase
import kindleclock.domain.model.device.Resolution
import kindleclock.primary.action.AuthenticatedActionBuilder
import kindleclock.primary.input.ResolutionParser
import kindleclock.primary.presenter.ShowClockPresenter
import kindleclock.primary.presenter.ShowInfoPresenter
import play.api.Logging
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ShowKindleClockController @Inject() (
  showInfoPresenter: ShowInfoPresenter,
  getKindleClockInfoUsecase: GetKindleClockInfoUsecase,
  showClockPresenter: ShowClockPresenter,
  cc: ControllerComponents,
  authenticatedActionBuilder: AuthenticatedActionBuilder,
  resolutionParser: ResolutionParser
)(implicit
  ec: ExecutionContext
) extends AbstractController(cc)
  with Logging {

  def clock: Action[AnyContent] =
    authenticatedActionBuilder.action.async { _ =>
      showClockPresenter.result(
        Resolution(
          width = 758,
          height = 1024
        )
      )
    }

  def show: Action[AnyContent] = showInternal(None)

  private def showInternal(resolutionOpt: Option[String]): Action[AnyContent] = {
    val defaultResolution = Resolution(
      width = 758,
      height = 1024
    )

    authenticatedActionBuilder.action.async { _ =>
      resolutionOpt.map(resolutionParser.parse) match {
        case Some(Left(errorResult)) =>
          Future.successful(errorResult)
        case parsedResolutionOpt =>
          (for {
            roomInfo <- getKindleClockInfoUsecase.execute
            result <- showInfoPresenter.result(
              roomInfo,
              parsedResolutionOpt match {
                case Some(Right(value)) => value
                case _ => defaultResolution
              }
            )
          } yield result).recoverWith { case e: Throwable =>
            logger.error(e.getMessage, e)

            Future.failed(e)
          }
      }
    }
  }
}
