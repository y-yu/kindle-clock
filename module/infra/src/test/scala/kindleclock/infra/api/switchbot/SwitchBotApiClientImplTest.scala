package kindleclock.infra.api.switchbot

import kindleclock.domain.model.Humidity
import kindleclock.domain.model.Temperature
import kindleclock.domain.model.switchbot.SwitchBotDeviceType
import kindleclock.domain.model.switchbot.SwitchBotMeterInfo
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

class SwitchBotApiClientImplTest extends AnyFlatSpec with Diagrams {
  import SwitchBotApiClientImpl.*

  "deviceListReads" should "parse an example JSON successfully" in {
    val expectedDeviceId = "500291B269BE"
    val json = Json.parse(
      s"""{
        |  "statusCode": 100,
        |  "body": {
        |    "deviceList": [
        |      {
        |        "deviceId": "$expectedDeviceId",
        |        "deviceName": "Living Room Humidifier",
        |        "deviceType": "Humidifier",
        |        "enableCloudService": true,
        |        "hubDeviceId": "000000000000"
        |      }
        |    ],
        |    "infraredRemoteList": [
        |      {
        |        "deviceId": "02-202008110034-13",
        |        "deviceName": "Living Room TV",
        |        "remoteType": "TV",
        |        "hubDeviceId": "FA7310762361"
        |      }
        |    ]
        |  },
        |  "message": "success"
        |}""".stripMargin
    )

    val actual = Json.fromJson[Map[SwitchBotDeviceType, String]](json)

    assert(actual.isSuccess)
    actual.foreach { m =>
      assert(m.size === 1)

      assert(
        m.get(SwitchBotDeviceType.Other("Humidifier")) ===
          Some(expectedDeviceId)
      )
    }
  }

  "switchBotMeterInfoReads" should "parse an example JSON successfully" in {
    val expectedDeviceId = "500291B269BE"
    val expectedHumidity = 49
    val expectedTemperature = 24.6
    val json = Json.parse(
      s"""{
         |  "statusCode": 100,
         |  "body": {
         |    "deviceId": "$expectedDeviceId",
         |    "deviceType": "MeterPlus",
         |    "hubDeviceId": "F1A5D0A81EBE",
         |    "humidity": $expectedHumidity,
         |    "temperature": $expectedTemperature
         |  },
         |  "message": "success"
         |}""".stripMargin
    )

    val actual = Json.fromJson[SwitchBotMeterInfo](json)
    assert(actual.isSuccess)
    assert(
      actual === JsSuccess(
        SwitchBotMeterInfo(
          deviceId = expectedDeviceId,
          temperature = Temperature(expectedTemperature),
          humidity = Humidity(expectedHumidity)
        )
      )
    )
  }
}
