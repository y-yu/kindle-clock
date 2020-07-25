package kindleclock.infra.api.openweathermap

import java.time.ZonedDateTime
import kindleclock.domain.model.WeatherIcon.Sunny
import kindleclock.domain.model.openweathermap.OpenWeatherMapInfo
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Json

class OpenWeatherMapApiClientImplTest extends AnyFlatSpec with Diagrams {
  "openWeatherMapInfoReads" should "parse the data from JSON" in {
    val json = Json.parse(
      """{
        |  "coord": {
        |    "lon": -122.08,
        |    "lat": 37.39
        |  },
        |  "weather": [
        |    {
        |      "id": 800,
        |      "main": "Clear",
        |      "description": "clear sky",
        |      "icon": "01d"
        |    }
        |  ],
        |  "base": "stations",
        |  "main": {
        |    "temp": 282.55,
        |    "feels_like": 281.86,
        |    "temp_min": 280.37,
        |    "temp_max": 284.26,
        |    "pressure": 1023,
        |    "humidity": 100
        |  },
        |  "visibility": 16093,
        |  "wind": {
        |    "speed": 1.5,
        |    "deg": 350
        |  },
        |  "clouds": {
        |    "all": 1
        |  },
        |  "dt": 1560350645,
        |  "sys": {
        |    "type": 1,
        |    "id": 5122,
        |    "message": 0.0139,
        |    "country": "US",
        |    "sunrise": 1560343627,
        |    "sunset": 1560396563
        |  },
        |  "timezone": -25200,
        |  "id": 420006353,
        |  "name": "Mountain View",
        |  "cod": 200
        |}                         
        |""".stripMargin
    )

    val actual = Json.fromJson(json)(OpenWeatherMapApiClientImpl.openWeatherMapInfoReads)

    assert(actual.isSuccess)
    actual.foreach { d =>
      assert(d == OpenWeatherMapInfo(Sunny, ZonedDateTime.parse("2019-06-12T23:44:05+09:00[Asia/Tokyo]")))
    }
  }
}
