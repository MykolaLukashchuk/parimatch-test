import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.ExecutionContext

class RouteTest extends AnyWordSpec with Matchers with ScalatestRouteTest with MockFactory{
  val Service = mock[Service]
  val Routes = new Routes()(Service, ExecutionContext.global).route
  "The service" should {

    "successful response" in {
      val data = "3, 5, 2, -4, 8, 11"
      lazy val sum = 7
      val expected = Seq(Seq(Seq(11, -4), Seq(2, 5)))
      (Service.processData _).expects(Some(sum -> data)).returning(Option(expected)).once()
      Post(s"/upload/$sum/", data) ~> Routes ~> check {
        response.status shouldBe StatusCodes.OK
        responseAs[String] shouldEqual "List(List(List(11, -4), List(2, 5)))"
      }
    }

    "unsuccessful response" in {
      val data = "3, 5, 2, -4, 8, 11, a"
      lazy val sum = 7
      (Service.processData _).expects(Some(sum -> data)).throws(new NumberFormatException("Error message")).once()
      Post(s"/upload/$sum/", data) ~> Routes ~> check {
        response.status shouldBe StatusCodes.BadRequest
        responseAs[String] shouldEqual """{ "code": "wrong.input.type", "message": "java.lang.NumberFormatException: Error message" }"""
      }
    }
  }
}
