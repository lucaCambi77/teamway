package it.cambi.workplanning

import cats.effect.{IO, IOApp, Resource}
import io.circe.generic.auto.*
import io.circe.syntax.*
import it.cambi.workplanning.domain.{Shift, Worker}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.circe.{jsonEncoder, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

implicit val shiftDecoder: EntityDecoder[IO, Shift] = jsonOf[IO, Shift]
implicit val workDecoder: EntityDecoder[IO, Worker] = jsonOf[IO, Worker]

object WorkPlanningApp extends IOApp.Simple with Http4sDsl[IO] {
  private val shiftService = new WorkerShiftService()

  // Define HTTP routes
  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "shifts" =>
      Ok(shiftService.getShifts.values.toList.asJson)

    case req@POST -> Root / "shifts" =>
      for {
        shift <- req.as[Shift] // Decode the request body into a Shift
        response <- shiftService.addShift(shift) match {
          case Right(_) => Created(shift) // If successful, respond with 201 Created
          case Left(e: IllegalArgumentException) => BadRequest(e.getMessage) // Handle the error and respond with 400 Bad Request
          case Left(_) => InternalServerError("An unexpected error occurred.") // Catch-all for other errors
        }
      } yield response

    case GET -> Root / "workers" =>
      Ok(shiftService.getWorkers.values.toList.asJson)

    case req@POST -> Root / "workers" =>
      for {
        worker <- req.as[Worker] // Decode the request body into a Worker
        response <- shiftService.addWorker(worker) match {
          case Right(_) => Created(worker) // If successful, respond with 201 Created
          case Left(e: IllegalArgumentException) => BadRequest(e.getMessage) // Handle the error and respond with 400 Bad Request
          case Left(_) => InternalServerError("An unexpected error occurred.") // Catch-all for other errors
        }
      } yield response
  }

  // Creating the server as a Resource
  def createServer: Resource[IO, org.http4s.server.Server] = {
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpRoutes.orNotFound)
      .resource
  }

  // Setting up the server
  val run: IO[Unit] = createServer.use { server =>
    IO(println("Server is running...")) *> IO.never // Keep the server running
  }
}


