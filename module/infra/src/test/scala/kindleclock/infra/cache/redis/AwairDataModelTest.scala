package kindleclock.infra.cache.redis

import kindleclock.infra.datamodel.awair.AwairDataModel
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec

class AwairDataModelTest extends AnyFlatSpec with Diagrams {
  "byteStringFormatter" should "be equal the original when deserialize the serialized data" in {
    val data = AwairDataModel(
      55,
      1.0,
      2.0,
      3,
      4,
      5.0
    )

    val sut = RedisCacheClientImpl.byteStringFormatter

    assert(sut.deserialize(sut.serialize(data)) == data)
  }
}
