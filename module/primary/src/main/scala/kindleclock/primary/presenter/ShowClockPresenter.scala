package kindleclock.primary.presenter

import java.time.Clock
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.KindleClockColor
import kindleclock.domain.model.device.Resolution
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import scala.concurrent.Future

class ShowClockPresenter @Inject(
  clock: Clock,
  svgToPngTransformer: SvgToPngTransformer
) {
  def result(
    resolution: Resolution
  ): Future[Result] = {
    val dateFormatter = DateTimeFormatter
      .ofPattern("EEE, MMM d, YYYY", Locale.ENGLISH)
    val clockFormatter = DateTimeFormatter
      .ofPattern("HH:mm", Locale.ENGLISH)

    val instant = clock.instant()
    val nowJST = instant.atZone(DefaultTimeZone.jst)
    val nowUTC = instant.atZone(ZoneOffset.UTC)

    val transform =
      Seq(
        s"rotate(90, ${resolution.width / 2}, ${resolution.height / 2})"
      ).mkString(" ")

    val svg =
      <svg width={resolution.width.toString} height={resolution.height.toString} xmlns="http://www.w3.org/2000/svg">
        <style>
          text {{ fill: black; }}
          .imageColor {{ fill: black; }}
        </style>
        <g transform={transform}>
          <title>Kindle Clock</title>
  
          <g font-family="DejaVu Sans">
            <text font-size="100px" y="300" x="380" text-anchor="middle">
              {nowJST.format(dateFormatter)}
            </text>

            <text font-size="350px" y="680" x="380" text-anchor="middle">
              {nowJST.format(clockFormatter)}
            </text>

            <text font-size="60px" y="850" x="380" text-anchor="middle">
              {nowUTC.format(DateTimeFormatter.ISO_DATE_TIME)}
            </text>
          </g>
        </g>
      </svg>

    Future.successful(
      Ok(
        svgToPngTransformer.transform(
          svg,
          KindleClockColor.White
        )
      ).as("image/png")
    )
  }
}
