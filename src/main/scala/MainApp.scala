import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContextExecutor

object MainApp {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    implicit val service: Service = new Service
    val route = new Routes().route
    Http().newServerAt("localhost", 8080).bind(route)
  }

}

