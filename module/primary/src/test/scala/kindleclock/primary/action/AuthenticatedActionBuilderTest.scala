package kindleclock.primary.action

import kindleclock.domain.model.config.auth.AuthenticationConfiguration
import org.scalatest.diagrams.Diagrams
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Writeable.*
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.Play.materializer
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

class AuthenticatedActionBuilderTest extends AnyWordSpec with GuiceOneAppPerSuite with Diagrams {
  trait SetUp {
    val mockControllerComponents = stubControllerComponents()

    def test(
      matrix: Map[(AuthenticationConfiguration, String), Int]
    ): Unit = {
      matrix.foreach { case (config, queryString) -> expected =>
        val sut = new AuthenticatedActionBuilder(
          config,
          mockControllerComponents
        )
        val fakeRequest = FakeRequest("GET", s"/$queryString")

        val actual = call(
          sut.action { _ =>
            Ok("ok")
          },
          fakeRequest
        )

        assert(status(actual)(1.seconds) == expected)
      }
    }
  }

  "action" should {
    "authenticate or reject successfully if the input token is valid" in new SetUp {
      test(
        Map(
          // OK
          (
            AuthenticationConfiguration(
              token = Some("test"),
              queryKeyName = ""
            ),
            "?=test"
          ) -> 200,
          // NG
          (
            AuthenticationConfiguration(
              token = Some("test"),
              queryKeyName = ""
            ),
            "?=invalid"
          ) -> 401,
          // config has query parameter key
          (
            AuthenticationConfiguration(
              token = Some("test"),
              queryKeyName = "key"
            ),
            "?key=test"
          ) -> 200,
          // token is none
          (
            AuthenticationConfiguration(
              token = None,
              queryKeyName = "key"
            ),
            "?NOT_Key=test"
          ) -> 200,
          // OK even if token is none but key exists
          (
            AuthenticationConfiguration(
              token = None,
              queryKeyName = "key"
            ),
            "?key=test"
          ) -> 200
        )
      )
    }
  }
}
