import Service.{Request, Result, StringAdditional}

import scala.concurrent.ExecutionContextExecutor

class Service(implicit executionContext: ExecutionContextExecutor) {

  private def bindInput(data: => String): Seq[Seq[Int]] = data.splitRows.map {
    str => str.splitByComma.map(_.trim).map(_.toInt)
  }

  private def findNumbers(seq: Seq[Int], sum: => Int, result: Seq[Seq[Int]] = Seq()): Seq[Seq[Int]] = {
    lazy val first = seq.head
    val other = seq.tail
    if (other.isEmpty) result
    else findNumbers(
      other,
      sum,
      other.foldLeft(result)((result, number) => if (number + first == sum) result :+ Seq(first, number) else result),
    )
  }

  def processData(request: Option[Request]): Option[Result] = {
   for {
     (sum, data) <- request
     boundData = bindInput(data)
     result = boundData.map(findNumbers(_, sum))
   } yield {
     result
   }
  }
}

object Service {
  implicit class StringAdditional(val s: String) extends AnyVal {
    def splitRows: Seq[String] = s.split('\n').toSeq

    def splitByComma: Seq[String] = s.split(',').toSeq
  }

  type Sum = Int
  type Data = String
  type Request = (Sum, Data)
  type Result = Seq[Seq[Seq[Int]]]
}