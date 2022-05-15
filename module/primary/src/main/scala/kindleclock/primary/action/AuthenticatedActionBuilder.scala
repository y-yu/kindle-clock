package kindleclock.primary.action

import javax.inject.Inject
import kindleclock.domain.model.config.auth.AuthenticationConfiguration
import play.api.mvc.AbstractController
import play.api.mvc.ActionBuilder
import play.api.mvc.ActionRefiner
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class AuthenticatedActionBuilder @Inject(
  authenticationConfiguration: AuthenticationConfiguration,
  cc: ControllerComponents
)(implicit
  ec: ExecutionContext
) extends AbstractController(cc) {

  val action: ActionBuilder[Request, AnyContent] =
    Action.andThen(
      new ActionRefiner[Request, Request] {
        override protected def refine[A](
          request: Request[A]
        ): Future[Either[Result, Request[A]]] =
          Future.successful {
            (
              request.getQueryString(authenticationConfiguration.queryKeyName),
              authenticationConfiguration.token
            ) match {
              case (Some(inputToken), Some(expectedToken)) =>
                if (safeEquals(inputToken, expectedToken))
                  Right(request)
                else
                  unauthorized
              case (_, None) =>
                Right(request)
              case _ =>
                unauthorized
            }
          }

        override protected def executionContext: ExecutionContext = ec
      }
    )

  private val unauthorized = Left(Unauthorized("The input token was not authenticated!"))

  /** Against timing attack, this is copy and paste from:
    * @see
    *   [[https://github.com/playframework/playframework/blob/1eddf37167c38d8713a15c230e6240eb11e74aef/core/play/src/main/scala/play/api/mvc/Cookie.scala#L582-L592]]
    */
  def safeEquals(a: String, b: String) = {
    if (a.length != b.length) {
      false
    } else {
      var equal = 0
      for (i <- Array.range(0, a.length)) {
        equal |= a(i) ^ b(i)
      }
      equal == 0
    }
  }
}
