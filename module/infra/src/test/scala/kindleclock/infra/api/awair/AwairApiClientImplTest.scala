package kindleclock.infra.api.awair

import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature
import kindleclock.domain.model.awair.Co2
import kindleclock.domain.model.awair.Pm25
import kindleclock.domain.model.awair.Score
import kindleclock.domain.model.awair.Voc
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Json

class AwairApiClientImplTest extends AnyFlatSpec with Diagrams {
  "electricEnergyReads" should "parse `AwairRoomInfo` from JSON" in {
    val json = Json.parse(
      """{
        |  "data": [
        |    {
        |      "timestamp": "2019-07-22T19:12:45.617Z",
        |      "score": 90,
        |      "sensors": [
        |        {
        |          "comp": "temp",
        |          "value": 24.49999961853027
        |        },
        |        {
        |          "comp": "humid",
        |          "value": 49.999999618530275
        |        },
        |        {
        |          "comp": "co2",
        |          "value": 419
        |        },
        |        {
        |          "comp": "voc",
        |          "value": 399
        |        },
        |        {
        |          "comp": "pm25",
        |          "value": 5
        |        },
        |        {
        |          "comp": "lux",
        |          "value": 123.79999961853028
        |        },
        |        {
        |          "comp": "spl_a",
        |          "value": 51.29999961853027
        |        }
        |      ],
        |      "indices": [
        |        {
        |          "comp": "temp",
        |          "value": 0
        |        },
        |        {
        |          "comp": "humid",
        |          "value": 0
        |        },
        |        {
        |          "comp": "co2",
        |          "value": 0
        |        },
        |        {
        |          "comp": "voc",
        |          "value": 1
        |        },
        |        {
        |          "comp": "pm25",
        |          "value": 4
        |        }
        |      ]
        |    }
        |  ]
        |}
        |""".stripMargin
    )

    val actual = Json.fromJson(json)(AwairApiClientImpl.electricEnergyReads)

    assert(actual.isSuccess)
    actual.foreach { awairRoomInfo =>
      assert(awairRoomInfo.score == Score(90))
      assert(awairRoomInfo.temperature == Temperature(24.49999961853027))
      assert(awairRoomInfo.humidity == Humidity(49.999999618530275))
      assert(awairRoomInfo.co2 == Co2(419))
      assert(awairRoomInfo.voc == Voc(399))
      assert(awairRoomInfo.pm25 == Pm25(5.0))
    }
  }
}
