package kindleclock.primary.presenter

import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.time.Clock
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kindleclock.domain.interfaces.usecase.GetKindleClockInfoUsecase.ShowKindleImageUsecaseResult
import kindleclock.domain.lib.DefaultTimeZone
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.util.XMLResourceDescriptor
import play.api.mvc.Result
import scala.concurrent.Future
import scala.xml.Elem
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import play.api.mvc.Results.InternalServerError
import play.api.mvc.Results.Ok
import scala.util.control.NonFatal

class KindleClockPresenter @Inject() (
  clock: Clock
) {
  def result(
    arg: ShowKindleImageUsecaseResult
  ): Future[Result] = {
    val now = OffsetDateTime.now(clock.withZone(DefaultTimeZone.jst))

    val svg = template(
      arg,
      now
    )

    val doc =
      new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName)
        .createSVGDocument(
          null, // Is it actually OK?
          new StringReader(svg.toString)
        )

    val transcoderInput = new TranscoderInput(doc)

    val pngStream = new ByteArrayOutputStream
    val t = new PNGTranscoder()
    t.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);

    Future.successful(
      try {
        val output = new TranscoderOutput(pngStream)
        t.transcode(transcoderInput, output)

        Ok(pngStream.toByteArray)
          .as("image/" + "png")
      } catch {
        case NonFatal(e) =>
          InternalServerError(
            e.getMessage
          )
      } finally {
        pngStream.close()
      }
    )
  }

  private def template(
    result: ShowKindleImageUsecaseResult,
    now: OffsetDateTime
  ): Elem = {
    val dateFormatter = DateTimeFormatter
      .ofPattern("EEE, MMM d", Locale.ENGLISH)
    val clockFormatter = DateTimeFormatter
      .ofPattern("HH:mm", Locale.ENGLISH)

    <svg width="758" height="1024" xmlns="http://www.w3.org/2000/svg">
      <g>
        <g transform="scale(3) translate(15.055 65.166)">
          {result.openWeatherMapInfo.weatherIcon.svg}
        </g>
        <title>Weather</title>

        <g font-family="DejaVu Sans">
          <text font-size="110px" y="152" x="380" text-anchor="middle">
            {now.format(dateFormatter)}
          </text>

          <text font-size="35px"  y="230" x="560" text-anchor="middle">Score:</text>
          <text font-size="100px" y="320" x="560" text-anchor="middle">
            {result.awairRoomInfo.score.value}
          </text>

          <text font-size="35px"  y="400" x="540" text-anchor="middle">Temperature:</text>

          <text font-size="60px"  y="459" x="515" text-anchor="end">
            {doubleSawedOffString(result.awairRoomInfo.temperature.value)}
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
            {doubleSawedOffString(result.awairRoomInfo.humidity.value)}
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

          <text font-size="35px"  y="723" x="50"  text-anchor="middle">CO<tspan baseline-shift="sub" font-size="30">2</tspan>:</text>
          <text font-size="64px"  y="783" x="180" text-anchor="end">
            {result.awairRoomInfo.co2.value}
          </text>
          <text font-size="35px"  y="783" x="220" text-anchor="middle">ppm</text>

          <text font-size="35px"  y="723" x="300" text-anchor="middle">VOC:</text>
          <text font-size="64px"  y="783" x="440" text-anchor="end">
            {result.awairRoomInfo.voc.value}
          </text>
          <text font-size="35px"  y="783" x="475" text-anchor="middle">ppb</text>

          <text font-size="35px"  y="723" x="575" text-anchor="middle">PM2.5:</text>
          <text font-size="64px"  y="783" x="630" text-anchor="end">
            {doubleSawedOffString(result.awairRoomInfo.pm25.value)}
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
