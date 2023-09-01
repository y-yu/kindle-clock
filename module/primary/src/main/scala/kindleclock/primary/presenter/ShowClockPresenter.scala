package kindleclock.primary.presenter

import java.time.Clock
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.device.Resolution
import kindleclock.primary.util.BackgroundColorDetector
import kindleclock.primary.util.image.SvgToPngTransformer
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import scala.concurrent.Future
import scala.xml.Elem

class ShowClockPresenter @Inject() (
  clock: Clock,
  svgToPngTransformer: SvgToPngTransformer,
  backgroundColorDetector: BackgroundColorDetector
) {
  private val fontSetting: Elem =
    <defs>
      <style>
        @import url("https://fonts.googleapis.com/css2?family=Dosis&amp;display=swap");
      </style>
    </defs>

  def result(
    resolution: Resolution
  ): Future[Result] = {
    val backgroundColorAndStyle = backgroundColorDetector.execute

    val dateFormatter = DateTimeFormatter
      .ofPattern("EEE, MMM d, YYYY", Locale.ENGLISH)
    val clockHourFormatter = DateTimeFormatter
      .ofPattern("HH", Locale.ENGLISH)
    val clockMinuteFormatter = DateTimeFormatter
      .ofPattern("mm", Locale.ENGLISH)

    val instant = clock.instant()
    val nowJST = instant.atZone(DefaultTimeZone.jst)

    val transform =
      Seq(
        s"rotate(-90, ${resolution.width / 2}, ${resolution.height / 2})"
      ).mkString(" ")

    val svg =
      <svg width={resolution.width.toString} height={resolution.height.toString} xmlns="http://www.w3.org/2000/svg">
        {fontSetting}
        {backgroundColorAndStyle.fontStyle}

        <title>Kindle Clock</title>
        <g transform={transform} font-family="Dosis">
          <text font-size="140px" y="300" x="380" text-anchor="middle">
            {nowJST.format(dateFormatter)}
          </text>

          <text font-size="390px" y="720" x="380" text-anchor="middle">
            <tspan>{nowJST.format(clockHourFormatter)}</tspan>
            <tspan dy="-0.15em">:</tspan>
            <tspan dy="0.15em">{nowJST.format(clockMinuteFormatter)}</tspan>
          </text>
        </g>
      </svg>

    Future.successful(
      Ok(
        svgToPngTransformer.transform(
          svg,
          backgroundColorAndStyle.background,
          kindleclock.primary.controller.routes.ShowKindleClockController.clock.url()
        )
      ).as("image/png")
    )
  }
}
