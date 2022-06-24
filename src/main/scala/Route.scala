import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.server.{Directives, Route}

import scala.concurrent.ExecutionContext

class Routes(implicit service: Service, executionContext: ExecutionContext) extends Directives {

  val route: Route = Route.seal(
    pathPrefix("upload" / IntNumber) { sum =>
      pathEndOrSingleSlash {
        post {
          entity(as[String]) { data =>
            try {
              val responseBody = service.processData(Some((sum, data))).map(r => s"$r").get
              complete(StatusCodes.OK, responseBody)
            } catch {
              case ex: NumberFormatException => complete(
                StatusCodes.BadRequest,
                Seq(`Content-Type`(`application/json`)),
                s"""{ "code": "wrong.input.type", "message": "${ex.toString.replace('\"', '\'')}" }""")
            }
          }
        }
      }
    }
  )
}
