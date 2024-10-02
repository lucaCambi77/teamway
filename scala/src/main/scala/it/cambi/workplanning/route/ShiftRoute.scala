package it.cambi.workplanning.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import it.cambi.workplanning.WorkerShiftService
import it.cambi.workplanning.domain.ShitJsonSupport.*
import it.cambi.workplanning.domain.{Shift, ShitJsonSupport}

class ShiftRoute(shiftService: WorkerShiftService) {

  val routes: Route = pathPrefix("shifts") {
    post {
      entity(as[Shift]) { shift =>
        onComplete(shiftService.addShift(shift)) {
          case scala.util.Success(message) => complete(StatusCodes.Created, message)
          case scala.util.Failure(ex: IllegalArgumentException) => complete(StatusCodes.BadRequest, ex.getMessage)
          case scala.util.Failure(_) => complete(StatusCodes.InternalServerError)
        }
      }
    }
  }
}

