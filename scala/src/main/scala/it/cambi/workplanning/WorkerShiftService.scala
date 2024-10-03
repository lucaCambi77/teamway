package it.cambi.workplanning

import it.cambi.workplanning.domain.{Shift, Worker, shiftType}

class WorkerShiftService {
  private var shifts: Map[Int, Shift] = Map()
  private var workers: Map[Int, Worker] = Map()

  def addShift(shift: Shift): Either[Throwable, Unit] = {
    if (!workers.contains(shift.workerId)) {
      Left(new IllegalArgumentException(s"Worker with ID ${shift.workerId} does not exist."))
    } else if (shifts.exists { case (_, existingShift) =>
      existingShift.workerId == shift.workerId && existingShift.day == shift.day
    }) {
      Left(new IllegalArgumentException(s"Worker with ID ${shift.workerId} already has a shift on ${shift.day}."))
    } else if (!shiftType.contains(shift.shiftType)) {
      Left(new IllegalArgumentException(s"Invalid shift type: ${shift.shiftType}. Allowed values: ${shiftType.mkString(", ")}."))
    } else {
      shifts = shifts.updated(shift.workerId, shift)
      Right(())
    }
  }

  def getShifts: Map[Int, Shift] = shifts

  def addWorker(worker: Worker): Either[Throwable, Unit] = {
    workers = workers.updated(worker.id, worker)
    Right(())
  }

  def getWorkers: Map[Int, Worker] = workers
}


