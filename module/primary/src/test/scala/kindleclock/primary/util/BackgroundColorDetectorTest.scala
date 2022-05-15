package kindleclock.primary.util

import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter
import kindleclock.domain.lib.SmartMock
import kindleclock.domain.model.KindleClockColor
import org.mockito.Mockito.when
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec

class BackgroundColorDetectorTest extends AnyFlatSpec with Diagrams with SmartMock {
  trait SetUp {
    val mockClock = smartMock[Clock]
    val sut = new BackgroundColorDetector(mockClock)
  }

  "execute" should "return White if the clock returns 8(JST)" in new SetUp {
    val instantString = "2022-05-15T08:00:00.00+09:00"
    val dummyInstant =
      Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(instantString))
    when(mockClock.instant())
      .thenReturn(dummyInstant)

    val actual = sut.execute

    assert(actual.background == KindleClockColor.White)
  }

  it should "return Black if the clock returns 20(JST)" in new SetUp {
    val instantString = "2022-05-15T20:00:00.00+09:00"
    val dummyInstant =
      Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(instantString))
    when(mockClock.instant())
      .thenReturn(dummyInstant)

    val actual = sut.execute

    assert(actual.background == KindleClockColor.Black)
  }
}
