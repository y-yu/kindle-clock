package kindleclock.primary.controller

import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import javax.inject.Inject
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase
import kindleclock.domain.model.device.Resolution
import kindleclock.primary.action.AuthenticatedActionBuilder
import kindleclock.primary.input.ResolutionParser
import kindleclock.primary.presenter.KindleClockPresenter
import play.api.Logging
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ShowKindleClockController @Inject() (
  kindleClockPresenter: KindleClockPresenter,
  getKindleClockInfoUsecase: GetKindleClockInfoUsecase,
  cc: ControllerComponents,
  authenticatedActionBuilder: AuthenticatedActionBuilder,
  resolutionParser: ResolutionParser
)(implicit
  ec: ExecutionContext
) extends AbstractController(cc)
  with Logging {

  private val defaultResolution = Resolution(
    width = 758,
    height = 1024
  )

  def show: Action[AnyContent] = showInternal(None)

  def showWithResolution(resolution: String): Action[AnyContent] = showInternal(Some(resolution))

  private def showInternal(resolutionOpt: Option[String]): Action[AnyContent] = {
    authenticatedActionBuilder.action.async { _ =>
      resolutionOpt.map(resolutionParser.parse) match {
        case Some(Left(errorResult)) =>
          Future.successful(errorResult)
        case parsedResolutionOpt =>
          (for {
            roomInfo <- getKindleClockInfoUsecase.execute
            result <- kindleClockPresenter.result(
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
