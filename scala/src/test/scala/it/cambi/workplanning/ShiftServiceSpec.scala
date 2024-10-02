package it.cambi.workplanning

import it.cambi.workplanning.domain.Shift
import org.scalatest.RecoverMethods.recoverToSucceededIf
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ShiftServiceSpec extends AnyFlatSpec with Matchers {

  "ShiftService" should "add a valid shift" in {
    val shiftService = new WorkerShiftService
    val shift = Shift("worker1", "2024-10-10", "Morning")

    val result = shiftService.addShift(shift)

    result.map(res => res shouldEqual "Shift added")
  }

  it should "not add an invalid shift" in {
    val shiftService = new WorkerShiftService
    val invalidShift = Shift("worker1", "2024-10-10", "InvalidShiftType")

    recoverToSucceededIf[IllegalArgumentException] {
      shiftService.addShift(invalidShift)
    }
  }

  it should "not add a shift if the worker already has a shift on the same day" in {
    val shiftService = new WorkerShiftService
    val shift1 = Shift("worker1", "2024-10-10", "Morning")
    val shift2 = Shift("worker1", "2024-10-10", "Afternoon")

    shiftService.addShift(shift1)

    recoverToSucceededIf[IllegalArgumentException] {
      shiftService.addShift(shift2)
    }
  }
}
