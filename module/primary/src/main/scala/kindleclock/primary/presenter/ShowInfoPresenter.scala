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
import kindleclock.domain.model.switchbot.SwitchBotMeterInfo
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

    def getSwitchBotMeterInfo(
      f: SwitchBotMeterInfo => String
    ): String =
      result.switchBotMeterInfo.headOption.fold("N/A")(f)

    val clockFormatter = DateTimeFormatter
      .ofPattern("HH:mm", Locale.ENGLISH)

    val nowJST = clock.instant().atZone(DefaultTimeZone.jst)

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
      <title>Kindle Clock</title>

      <g font-family="DejaVu Sans">
        <g transform="scale(3) translate(15 0)" class="imageColor">
          {result.openWeatherMapInfo.weatherIcon.svg}
        </g>
        <text font-size="10px" y="270" x="490" text-anchor="end">
          {result.openWeatherMapInfo.updatedAt.toString}
        </text>

        <text font-size="50px"  y="70" x="590" text-anchor="middle">
          {nowJST.format(clockFormatter)}
        </text>
        
        <text font-size="35px"  y="160" x="590" text-anchor="middle">Score:</text>
        <text font-size="90px" y="260" x="590" text-anchor="middle">
          {getAwairRoomInfo(_.score.value.toString)}
        </text>

        <text y="360" x="20" font-size="30px">
          <tspan x="20" dy="2em">AWAIR</tspan>
          <tspan x="20" dy="4em">Nature</tspan>
          <tspan x="40" dy="1em">Remo</tspan>
          <tspan x="20" dy="4em">SwitchBot</tspan>
        </text>
        
        <text y="360" x="170" font-size="90px">
          <tspan x="200" dy="-1em" dx="0.5em" font-size="30px">Temperature</tspan>
          <tspan x="200" dy="1.2em">
            {getAwairRoomInfo(info => doubleSawedOffString(info.temperature.value))}
            <tspan font-size="40px" dx="-0.5em" text-anchor="end">°C</tspan>
          </tspan>
          <tspan x="200" dy="1.5em">
            {doubleSawedOffString(result.natureRemoRoomInfo.temperature.value)}
            <tspan font-size="40px" dx="-0.5em" text-anchor="end">°C</tspan>
          </tspan>
          <tspan x="200" dy="1.5em">
            {getSwitchBotMeterInfo(info => doubleSawedOffString(info.temperature.value))}
            <tspan font-size="40px" dx="-0.5em" text-anchor="end">°C</tspan>
          </tspan>
        </text>

        <text y="360" x="520" font-size="90px">
          <tspan x="490" dy="-1em" dx="1.2em" font-size="30px">Humidity</tspan>
          <tspan x="490" dy="1.2em">
            {getAwairRoomInfo(info => doubleSawedOffString(info.humidity.value))}
            <tspan font-size="40px" dx="-0.5em" text-anchor="end">%</tspan>
          </tspan>
          <tspan x="490" dy="1.5em">
            {doubleSawedOffString(result.natureRemoRoomInfo.humidity.value)}
            <tspan font-size="40px" dx="-0.5em" text-anchor="end">%</tspan>
          </tspan>
          <tspan x="490" dy="1.5em">
            {getSwitchBotMeterInfo(info => doubleSawedOffString(info.humidity.value))}
            <tspan font-size="40px" dx="-0.5em" text-anchor="end">%</tspan>
          </tspan>
        </text>
        
        <text font-size="35px"  y="780" x="250" text-anchor="middle">Electric Energy:</text>
        <text font-size="90px"  y="850" x="565" text-anchor="end">
          {result.natureRemoRoomInfo.electricEnergy.value}
        </text>
        <text font-size="64px"  y="850" x="570" text-anchor="start">W</text>

        <text font-size="35px"  y="923" x="50"  text-anchor="middle">CO<tspan baseline-shift="sub" font-size="25">2</tspan>:</text>
        <text font-size="64px"  y="993" x="180" text-anchor="end">
          {getAwairRoomInfo(_.co2.value.toString)}
        </text>
        <text font-size="35px"  y="983" x="220" text-anchor="middle">ppm</text>

        <text font-size="35px"  y="923" x="300" text-anchor="middle">VOC:</text>
        <text font-size="64px"  y="993" x="440" text-anchor="end">
          {getAwairRoomInfo(_.voc.value.toString)}
        </text>
        <text font-size="35px"  y="983" x="475" text-anchor="middle">ppb</text>

        <text font-size="35px"  y="923" x="575" text-anchor="middle">PM2.5:</text>
        <text font-size="64px"  y="993" x="630" text-anchor="end">
          {getAwairRoomInfo(info => doubleSawedOffString(info.pm25.value))}
        </text>
        <text font-size="35px"  y="983" x="690" text-anchor="middle">μg/m<tspan baseline-shift="super">3</tspan></text>
      </g>
    </svg>
  }

  private def doubleSawedOffString(
    d: Double
  ): String = f"$d%2.1f"
}
