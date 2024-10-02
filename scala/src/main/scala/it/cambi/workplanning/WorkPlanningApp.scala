package it.cambi.workplanning

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import it.cambi.workplanning.route.{ShiftRoute, WorkerRoute}

import scala.concurrent.ExecutionContextExecutor

object WorkPlanningApp extends App {
  implicit val system: ActorSystem = ActorSystem("work-planning-service")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val workerShiftService = new WorkerShiftService()

  private val workerRoutes = new WorkerRoute(workerShiftService).routes
  private val shiftRoutes = new ShiftRoute(workerShiftService).routes

  private val routes = workerRoutes ~ shiftRoutes

  Http().newServerAt("localhost", 8080).bind(routes)
  println("Server online at http://localhost:8080/")
}
