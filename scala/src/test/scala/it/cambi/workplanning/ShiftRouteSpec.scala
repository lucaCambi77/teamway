package it.cambi.workplanning

import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.cambi.workplanning.domain.Shift
import it.cambi.workplanning.domain.ShitJsonSupport._
import it.cambi.workplanning.route.ShiftRoute
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ShiftRouteSpec extends AnyFlatSpec with ScalatestRouteTest with Matchers {

  val shiftService = new WorkerShiftService()
  val route: Route = new ShiftRoute(shiftService).routes

  "The Shift Service" should "add a new shift" in {
    val newShift = Shift("workerId123", "2024-10-10", "Morning")

    Post("/shifts", newShift) ~> route ~> check {
      status shouldEqual StatusCodes.Created
    }
  }

  it should "reject an invalid shift" in {
    val invalidShift = Shift("workerId123", "2024-10-10", "InvalidShiftType")

    Post("/shifts", invalidShift) ~> route ~> check {
      status shouldEqual StatusCodes.BadRequest
    }
  }
}
