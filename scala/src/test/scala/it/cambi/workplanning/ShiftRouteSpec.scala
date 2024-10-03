package it.cambi.workplanning

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import io.circe.generic.auto.*
import io.circe.syntax.*
import it.cambi.workplanning.domain.{Shift, Worker}
import org.http4s.*
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.*
import org.http4s.implicits.*
import org.http4s.server.Server
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class ShiftRouteSpec extends AnyFlatSpec with Matchers {

  // Helper method to create the server resource
  val serverResource: Resource[IO, Server] = WorkPlanningApp.createServer

  "WorkPlanningApp" should "add a worker and return the worker" in {
    val worker = Worker(1, "John Doe") // Adjust Worker constructor as needed
    val requestBody = worker.asJson

    // Use the server resource to create the server
    serverResource.use { _ =>
      // Create a client to send requests
      BlazeClientBuilder[IO](ExecutionContext.global).resource.use { client =>
        val postRequest = Request[IO](Method.POST, uri"http://localhost:8080/workers").withEntity(requestBody)

        // Send the request and check the response
        for {
          response <- client.fetch(postRequest)(r => IO.pure(r))
          body <- response.as[Worker] // Decode the response back into a Worker
        } yield {
          response.status shouldEqual Status.Created
          body shouldEqual worker // Check that the returned worker matches
        }
      }
    }.unsafeRunSync() // Run the IO action to completion
  }

  it should "return an error when adding a shift for a non-existing worker" in {
    val invalidShift = Shift(workerId = 99, day = "2024-10-02", shiftType = "Afternoon")
    val requestBody = invalidShift.asJson

    serverResource.use { _ =>
      // Create a client to send requests
      BlazeClientBuilder[IO](ExecutionContext.global).resource.use { client =>
        val postRequest = Request[IO](Method.POST, uri"http://localhost:8080/shifts").withEntity(requestBody)

        // Send the request and check the response
        for {
          response <- client.fetch(postRequest)(r => IO.pure(r))
          body <- response.as[String] // Decode the response into a String
        } yield {
          response.status shouldEqual Status.BadRequest // Adjust based on your error handling
          body should include("Worker with ID 99 does not exist.") // Check the error message
        }
      }
    }.unsafeRunSync() // Run the IO action to completion
  }

  "WorkPlanningApp" should "return an error if a worker already has a shift on the same day" in {
    val worker = Worker(1, "John Doe")
    val shift1 = Shift(workerId = 1, day = "2024-10-10", shiftType = "Morning")
    val shift2 = Shift(workerId = 1, day = "2024-10-10", shiftType = "Afternoon") // Same day, different shift

    serverResource.use { _ =>
      BlazeClientBuilder[IO](ExecutionContext.global).resource.use { client =>
        val addWorkerRequest = Request[IO](Method.POST, uri"http://localhost:8080/workers").withEntity(worker.asJson)
        val addShift1Request = Request[IO](Method.POST, uri"http://localhost:8080/shifts").withEntity(shift1.asJson)
        val addShift2Request = Request[IO](Method.POST, uri"http://localhost:8080/shifts").withEntity(shift2.asJson)

        for {
          // First, add the worker
          workerResponse <- client.fetch(addWorkerRequest)(r => IO.pure(r))
          _ = workerResponse.status shouldEqual Status.Created

          // Then, add the first shift (success)
          shift1Response <- client.fetch(addShift1Request)(r => IO.pure(r))
          _ = shift1Response.status shouldEqual Status.Created

          // Attempt to add the second shift for the same day (should fail)
          shift2Response <- client.fetch(addShift2Request)(r => IO.pure(r))
          responseBody <- shift2Response.as[String] // Get the error message

        } yield {
          shift2Response.status shouldEqual Status.BadRequest
          responseBody should include("Worker with ID 1 already has a shift on 2024-10-10.")
        }
      }
    }.unsafeRunSync()
  }

  "WorkPlanningApp" should "return an error if an invalid shift type is provided" in {
    val worker = Worker(1, "John Doe")
    val invalidShift = Shift(workerId = 1, day = "2024-10-11", shiftType = "InvalidShiftType")

    serverResource.use { _ =>
      BlazeClientBuilder[IO](ExecutionContext.global).resource.use { client =>
        val addWorkerRequest = Request[IO](Method.POST, uri"http://localhost:8080/workers").withEntity(worker.asJson)
        val addShiftRequest = Request[IO](Method.POST, uri"http://localhost:8080/shifts").withEntity(invalidShift.asJson)

        for {
          // First, add the worker
          workerResponse <- client.fetch(addWorkerRequest)(r => IO.pure(r))
          _ = workerResponse.status shouldEqual Status.Created

          // Then, attempt to add a shift with an invalid shift type
          shiftResponse <- client.fetch(addShiftRequest)(r => IO.pure(r))
          responseBody <- shiftResponse.as[String] // Get the error message

        } yield {
          shiftResponse.status shouldEqual Status.BadRequest
          responseBody should include("Invalid shift type: InvalidShiftType.")
        }
      }
    }.unsafeRunSync()
  }
}

