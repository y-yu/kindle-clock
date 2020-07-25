package kindleclock.infra.api.natureremo

import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Json

class NatureRemoApiClientImplTest extends AnyFlatSpec with Diagrams {

  "temperatureAndHumidityReads" should "parse the temperature and humidity from JSON" in {
    val json = Json.parse("""[
        |  {
        |    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        |    "name": "string",
        |    "temperature_offset": 0,
        |    "humidity_offset": 0,
        |    "created_at": "2020-07-24T10:11:20.098Z",
        |    "updated_at": "2020-07-24T10:11:20.098Z",
        |    "firmware_version": "string",
        |    "mac_address": "string",
        |    "serial_number": "string",
        |    "newest_events": {
        |      "te": {
        |        "val": 12.3,
        |        "created_at": "2020-07-24T10:11:20.098Z"
        |      },
        |      "hu": {
        |        "val": 45.6,
        |        "created_at": "2020-07-24T10:11:20.098Z"
        |      },
        |      "il": {
        |        "val": 0,
        |        "created_at": "2020-07-24T10:11:20.099Z"
        |      },
        |      "mo": {
        |        "val": 0,
        |        "created_at": "2020-07-24T10:11:20.099Z"
        |      }
        |    }
        |  }
        |]
        |""".stripMargin)

    val actual = Json.fromJson(json)(NatureRemoApiClientImpl.temperatureAndHumidityReads)

    assert(actual.isSuccess)
    actual.foreach { d =>
      assert(d._1.value == 12.3)
      assert(d._2.value == 45.6)
    }
  }

  "electricEnergyReads" should "arse the electric energy from JSON" in {
    val json = Json.parse("""[
        |  { },
        |  {
        |    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        |    "device": {
        |      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        |      "name": "string",
        |      "temperature_offset": 0,
        |      "humidity_offset": 0,
        |      "created_at": "2020-07-24T10:32:15.228Z",
        |      "updated_at": "2020-07-24T10:32:15.228Z",
        |      "firmware_version": "string",
        |      "mac_address": "string",
        |      "serial_number": "string"
        |    },
        |    "model": {
        |      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        |      "manufacturer": "string",
        |      "remote_name": "string",
        |      "name": "string",
        |      "image": "string"
        |    },
        |    "nickname": "string",
        |    "image": "string",
        |    "type": "AC",
        |    "settings": {
        |      "temp": "string",
        |      "mode": "",
        |      "vol": "",
        |      "dir": "",
        |      "button": ""
        |    },
        |    "aircon": {
        |      "range": {
        |        "modes": {
        |          "cool": {
        |            "temp": [
        |              "string"
        |            ],
        |            "vol": [
        |              ""
        |            ],
        |            "dir": [
        |              ""
        |            ]
        |          },
        |          "warm": {
        |            "temp": [
        |              "string"
        |            ],
        |            "vol": [
        |              ""
        |            ],
        |            "dir": [
        |              ""
        |            ]
        |          },
        |          "dry": {
        |            "temp": [
        |              "string"
        |            ],
        |            "vol": [
        |              ""
        |            ],
        |            "dir": [
        |              ""
        |            ]
        |          },
        |          "blow": {
        |            "temp": [
        |              "string"
        |            ],
        |            "vol": [
        |              ""
        |            ],
        |            "dir": [
        |              ""
        |            ]
        |          },
        |          "auto": {
        |            "temp": [
        |              "string"
        |            ],
        |            "vol": [
        |              ""
        |            ],
        |            "dir": [
        |              ""
        |            ]
        |          }
        |        },
        |        "fixedButtons": [
        |          ""
        |        ]
        |      },
        |      "tempUnit": ""
        |    },
        |    "signals": [
        |      {
        |        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        |        "name": "string",
        |        "image": "string"
        |      }
        |    ],
        |    "tv": {
        |      "state": {
        |        "input": "t"
        |      },
        |      "buttons": [
        |        {
        |          "name": "string",
        |          "image": "string",
        |          "label": "string"
        |        }
        |      ]
        |    },
        |    "light": {
        |      "state": {
        |        "brightness": "string",
        |        "power": "on",
        |        "last_button": "string"
        |      },
        |      "buttons": [
        |        {
        |          "name": "string",
        |          "image": "string",
        |          "label": "string"
        |        }
        |      ]
        |    },
        |    "smart_meter": {
        |    "echonetlite_properties": [
        |    {
        |      "name": "coefficient",
        |      "epc": 211,
        |      "val": "1",
        |      "updated_at": "2020-04-27T15:24:03Z"
        |    },
        |    {
        |      "name": "cumulative_electric_energy_effective_digits",
        |      "epc": 215,
        |      "val": "6",
        |      "updated_at": "2020-04-27T15:24:03Z"
        |    },
        |    {
        |      "name": "normal_direction_cumulative_electric_energy",
        |      "epc": 224,
        |      "val": "5167",
        |      "updated_at": "2020-04-27T15:24:03Z"
        |    },
        |    {
        |      "name": "cumulative_electric_energy_unit",
        |      "epc": 225,
        |      "val": "1",
        |      "updated_at": "2020-04-27T15:24:03Z"
        |    },
        |    {
        |      "name": "reverse_direction_cumulative_electric_energy",
        |      "epc": 227,
        |      "val": "3606",
        |      "updated_at": "2020-04-27T15:24:03Z"
        |    },
        |    {
        |      "name": "measured_instantaneous",
        |      "epc": 231,
        |      "val": "360",
        |      "updated_at": "2020-04-27T15:24:03Z"
        |    }
        |    ]}
        |  },
        |  { }
        |]
        |""".stripMargin)

    val actual = Json.fromJson(json)(NatureRemoApiClientImpl.electricEnergyReads)

    assert(actual.isSuccess)
    actual.foreach { e =>
      assert(e.value == 360)
    }
  }

}
