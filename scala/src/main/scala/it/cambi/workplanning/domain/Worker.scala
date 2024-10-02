package it.cambi.workplanning.domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Worker(id: String, name: String)

enum ShiftType:
  case Morning, Afternoon, Night

case class Shift(workerId: String, day: String, shiftType: String)

object WorkerJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val workerFormat: RootJsonFormat[Worker] = jsonFormat2(Worker.apply)
}

object ShitJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val shiftFormat: RootJsonFormat[Shift] = jsonFormat3(Shift.apply)
}