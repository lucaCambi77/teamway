package it.cambi.workplanning.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import it.cambi.workplanning.domain.{Worker, WorkerJsonSupport}
import it.cambi.workplanning.WorkerShiftService

class WorkerRoute(shiftService: WorkerShiftService)() {

  import WorkerJsonSupport._

  val routes: Route = pathPrefix("workers") {
    post {
      entity(as[Worker]) { worker =>
        onComplete(shiftService.addWorker(worker)) {
          case scala.util.Success(message) => complete(StatusCodes.Created, message)
          case scala.util.Failure(ex: IllegalArgumentException) => complete(StatusCodes.BadRequest, ex.getMessage)
          case scala.util.Failure(_) => complete(StatusCodes.InternalServerError)
        }
      }
    }
  }
}
