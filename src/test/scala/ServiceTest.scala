import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.postfixOps

class ServiceTest extends AnyFunSuite with MockFactory with Matchers {
  implicit val context: ExecutionContextExecutor = ExecutionContext.global
  val sut = new Service()

  test("successful processing") {
    val result = sut.processData(Some(7 -> "3, 5, 2, -4, 8, 11"))
    result.get shouldBe Seq(Seq(Seq(5, 2), Seq(-4, 11)))
  }

  test("successful processing 2") {
    val result = sut.processData(Some(9 -> "2, 7, 11, 15, 3, -2"))
    result.get shouldBe Seq(Seq(Seq(2, 7), Seq(11, -2)))
  }

  test("successful processing both") {
    val result = sut.processData(Some(9 -> "2, 7, 11, 15, 3, -2\n3, 5, 2, -4, 7, 11"))
    result.get shouldBe Seq(Seq(Seq(2, 7), Seq(11, -2)), Seq(Seq(2, 7)))
  }

  test("failed parsing") {
    try {
      sut.processData(Some(7 -> "3, 5, 2, -4, 8, a"))
    } catch {
      case ex: Exception => ex.toString shouldBe """java.lang.NumberFormatException: For input string: "a""""
    }
  }
}
