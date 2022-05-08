package kindleclock.primary.input

import kindleclock.domain.model.device.Resolution
import play.api.mvc.Result
import scala.util.control.NonFatal
import play.api.mvc.Results.*

class ResolutionParser {
  private val ResolutionFormat = "([1-9]\\d*)x([1-9]\\d*)".r

  def parse(input: String): Either[Result, Resolution] =
    input match {
      case ResolutionFormat(height, width) =>
        try {
          Right(Resolution(width.toInt, height.toInt))
        } catch {
          case NonFatal(e) =>
            Left(BadRequest(s"Resolution parse error! $input"))
        }

      case _ =>
        Left(BadRequest(s"Resolution format is `<height>x<width>`."))
    }

}
