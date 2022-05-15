package kindleclock.primary.controller

import javax.inject.Inject
import kindleclock.domain.lib.BuildInfo
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Results

class ShowBuildInfoController @Inject(
  cc: ControllerComponents
) extends AbstractController(cc) {
  def show: Action[AnyContent] =
    Action.apply { _ =>
      Results.Ok(
        Json.obj(
          "build_info" -> Json.obj(
            "scala_version" -> BuildInfo.scalaVersion,
            "commit_hash" -> BuildInfo.commitHash
          )
        )
      )
    }
}
