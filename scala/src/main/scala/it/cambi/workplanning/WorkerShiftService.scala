package it.cambi.workplanning

import it.cambi.workplanning.domain.{Shift, ShiftType, Worker}

import scala.concurrent.{ExecutionContext, Future}

class WorkerShiftService(implicit ec: ExecutionContext) {
  private var workers: List[Worker] = List()
  private var shifts: List[Shift] = List()

  // Add a new worker
  def addWorker(worker: Worker): Future[String] = Future {
    if (!workers.exists(_.id == worker.id)) {
      workers = workers :+ worker
      "Worker added successfully."
    } else {
      throw new IllegalArgumentException("Worker ID already exists.")
    }
  }

  // Add a new shift
  def addShift(shift: Shift): Future[String] = Future {
    if (isValidShift(shift)) {
      shifts = shifts :+ shift
      "Shift added successfully."
    } else {
      throw new IllegalArgumentException("Invalid shift.")
    }
  }

  private def isValidShift(shift: Shift): Boolean = {
    if (!ShiftType.values.contains(ShiftType.valueOf(shift.shiftType))) return false
    !shifts.exists(s => s.workerId == shift.workerId && s.day == shift.day)
  }
}

