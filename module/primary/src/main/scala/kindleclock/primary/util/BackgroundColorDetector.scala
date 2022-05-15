package kindleclock.primary.util

import java.time.Clock
import javax.inject.Inject
import kindleclock.domain.lib.DefaultTimeZone
import kindleclock.domain.model.KindleClockColor
import kindleclock.primary.util.BackgroundColorDetector.KindleClockColorWithFontStyle
import scala.xml.Elem

class BackgroundColorDetector @Inject() (
  clock: Clock
) {
  def execute: KindleClockColorWithFontStyle = {
    val zonedDateTime = clock.instant().atZone(DefaultTimeZone.jst)

    if (zonedDateTime.getHour <= 6 || zonedDateTime.getHour >= 17)
      KindleClockColorWithFontStyle(
        KindleClockColor.Black,
        <style>
          text {{ fill: white; }}
          .imageColor {{ fill: white; }}
        </style>
      )
    else {
      KindleClockColorWithFontStyle(
        KindleClockColor.White,
        <style>
          text {{ fill: black; }}
          .imageColor {{ fill: black; }}
        </style>
      )
    }
  }
}

object BackgroundColorDetector {
  case class KindleClockColorWithFontStyle(
    background: KindleClockColor,
    fontStyle: Elem
  )
}
