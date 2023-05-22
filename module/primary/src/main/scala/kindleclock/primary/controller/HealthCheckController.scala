package kindleclock.primary.controller

import jakarta.inject.Inject
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

class HealthCheckController @Inject() (
  cc: ControllerComponents
) extends AbstractController(cc) {
  def show: Action[AnyContent] = Action.apply { _ =>
    Ok("")
  }
}
