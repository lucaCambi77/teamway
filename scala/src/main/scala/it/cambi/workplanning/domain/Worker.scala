package it.cambi.workplanning.domain

case class Worker(id: Int, name: String)

val shiftType: List[String] = List("Morning", "Afternoon", "Night")

case class Shift(workerId: Int, day: String, shiftType: String)

