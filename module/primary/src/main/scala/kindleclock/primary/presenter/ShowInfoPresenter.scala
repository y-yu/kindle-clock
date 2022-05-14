package kindleclock.primary.presenter

import java.time.Clock
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.KindleClockColor
import kindleclock.domain.model.awair.AwairRoomInfo
import kindleclock.domain.model.device.Resolution
import play.api.mvc.Result
import scala.concurrent.Future
import scala.xml.Elem
import play.api.mvc.Results.Ok

class ShowInfoPresenter @Inject() (
  clock: Clock,
  svgToPngTransformer: SvgToPngTransformer
) {
  def result(
    arg: ShowKindleImageUsecaseResult,
    resolution: Resolution
  ): Future[Result] = {
    val now = OffsetDateTime.now(clock.withZone(DefaultTimeZone.jst))

    Future.successful(
      Ok(
        svgToPngTransformer.transform(
          template(
            arg,
            now,
            arg.backgroundColor == KindleClockColor.White,
            resolution
          ),
          arg.backgroundColor
        )
      ).as("image/png")
    )
  }

  private def template(
    result: ShowKindleImageUsecaseResult,
    now: OffsetDateTime,
    isFontColorBlack: Boolean,
    resolution: Resolution
  ): Elem = {
    def getAwairRoomInfo(
      f: AwairRoomInfo => String
    ): String =
      result.awairRoomInfo.fold(
        _ => "N/A",
        f
      )

    val dateFormatter = DateTimeFormatter
      .ofPattern("EEE, MMM d", Locale.ENGLISH)
    val clockFormatter = DateTimeFormatter
      .ofPattern("HH:mm", Locale.ENGLISH)

    val style = {
      if (isFontColorBlack)
        <style>
          text {{ fill: black; }}
          .imageColor {{ fill: black; }}
        </style>
      else
        <style>
          text {{ fill: white; }}
          .imageColor {{ fill: white; }}
        </style>
    }

    <svg width={resolution.width.toString} height={resolution.height.toString} xmlns="http://www.w3.org/2000/svg">
      {style}
      <g>
        <g transform="scale(3) translate(15.055 65.166)" class="imageColor">
          {result.openWeatherMapInfo.weatherIcon.svg}
        </g>
        <title>Kindle Clock</title>

        <g font-family="DejaVu Sans">
          <text font-size="110px" y="152" x="380" text-anchor="middle">
            {now.format(dateFormatter)}
          </text>

          <text font-size="35px"  y="230" x="560" text-anchor="middle">Score:</text>
          <text font-size="100px" y="320" x="560" text-anchor="middle">
            {getAwairRoomInfo(_.score.value.toString)}
          </text>

          <text font-size="35px"  y="400" x="540" text-anchor="middle">Temperature:</text>

          <text font-size="60px"  y="459" x="515" text-anchor="end">
            {getAwairRoomInfo(info => doubleSawedOffString(info.temperature.value))}
          </text>
          <text font-size="15px"  y="479" x="515" text-anchor="end">AWAIR</text>
          <text font-size="100px" y="489" x="520" text-anchor="start">/</text>
          <text font-size="15px"  y="439" x="555" text-anchor="start">Nature Remo</text>
          <text font-size="60px"  y="489" x="555" text-anchor="start">
            {doubleSawedOffString(result.natureRemoRoomInfo.temperature.value)}
          </text>
          <text font-size="40px"  y="489" x="735" text-anchor="end">°C</text>

          <text font-size="35px"  y="555" x="540" text-anchor="middle">Humidity:</text>

          <text font-size="60px"  y="610" x="515" text-anchor="end">
            {getAwairRoomInfo(info => doubleSawedOffString(info.humidity.value))}
          </text>
          <text font-size="15px"  y="630" x="515" text-anchor="end">AWAIR</text>
          <text font-size="100px" y="640" x="520" text-anchor="start">/</text>
          <text font-size="15px"  y="590" x="555" text-anchor="start">Nature Remo</text>
          <text font-size="60px"  y="640" x="555" text-anchor="start">
            {doubleSawedOffString(result.natureRemoRoomInfo.humidity.value)}
          </text>
          <text font-size="40px"  y="640" x="730" text-anchor="end">%</text>

          <text font-size="10px"  y="500" x="320" text-anchor="end">
            {result.openWeatherMapInfo.updatedAt.toString}
          </text>

          <text font-size="35px"  y="555" x="150" text-anchor="middle">Electric Energy:</text>
          <text font-size="90px"  y="640" x="265" text-anchor="end">
            {result.natureRemoRoomInfo.electricEnergy.value}
          </text>
          <text font-size="64px"  y="640" x="270" text-anchor="start">W</text>

          <text font-size="35px"  y="723" x="50"  text-anchor="middle">CO<tspan baseline-shift="sub" font-size="25">2</tspan>:</text>
          <text font-size="64px"  y="783" x="180" text-anchor="end">
            {getAwairRoomInfo(_.co2.value.toString)}
          </text>
          <text font-size="35px"  y="783" x="220" text-anchor="middle">ppm</text>

          <text font-size="35px"  y="723" x="300" text-anchor="middle">VOC:</text>
          <text font-size="64px"  y="783" x="440" text-anchor="end">
            {getAwairRoomInfo(_.voc.value.toString)}
          </text>
          <text font-size="35px"  y="783" x="475" text-anchor="middle">ppb</text>

          <text font-size="35px"  y="723" x="575" text-anchor="middle">PM2.5:</text>
          <text font-size="64px"  y="783" x="630" text-anchor="end">
            {getAwairRoomInfo(info => doubleSawedOffString(info.pm25.value))}
          </text>
          <text font-size="35px"  y="783" x="690" text-anchor="middle">μg/m<tspan baseline-shift="super">3</tspan></text>

          <text font-size="180px" y="960" x="380" text-anchor="middle">
            {now.format(clockFormatter)}
          </text>
        </g>
      </g>
    </svg>
  }

  private def doubleSawedOffString(
    d: Double
  ): String = f"$d%2.1f"
}
