package com.teamway.demo.repository;

import com.teamway.demo.domain.Shift;
import com.teamway.demo.domain.WorkerShift;
import com.teamway.demo.exception.WorkerShiftException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorkerShiftRepositoryTest {

  private WorkerRepository workerRepository;

  @BeforeEach
  public void setUp() {
    workerRepository = new WorkerRepository();
  }

  @Test
  public void shouldAddWorkerShift() throws WorkerShiftException {
    WorkerShift shift =
        WorkerShift.builder()
            .shift(Shift.FIRST)
            .shiftDay("2022-01-01")
            .workerName("Worker1")
            .build();

    workerRepository.addWorkerShift(shift);

    assertEquals(1, workerRepository.getWorkerShifts(shift.getWorkerName()).size());
    assertEquals(
        1, workerRepository.getWorkerShiftsByMonth(shift.getWorkerName(), "2022-01").size());
    assertEquals(
        1, workerRepository.getWorkerShiftsByDay(shift.getWorkerName(), "2022-01-01").size());
  }

  @Test
  public void shouldAddWorkersShift() throws WorkerShiftException {
    WorkerShift shift =
        WorkerShift.builder()
            .shift(Shift.FIRST)
            .shiftDay("2022-01-01")
            .workerName("Worker1")
            .build();

    WorkerShift shift1 =
        WorkerShift.builder()
            .shift(Shift.FIRST)
            .shiftDay("2022-01-02")
            .workerName("Worker2")
            .build();

    workerRepository.addWorkerShift(shift);
    workerRepository.addWorkerShift(shift1);

    assertEquals(2, workerRepository.getShiftsByMonth("2022-01").size());
    assertEquals(1, workerRepository.getShiftsByDay("2022-01-02").size());
    assertEquals(1, workerRepository.getShiftsByDay("2022-01-01").size());

    assertEquals(1, workerRepository.getWorkerShifts(shift.getWorkerName()).size());
    assertEquals(1, workerRepository.getWorkerShifts(shift1.getWorkerName()).size());

    assertEquals(
        1, workerRepository.getWorkerShiftsByDay(shift.getWorkerName(), "2022-01-01").size());
    assertEquals(
        1, workerRepository.getWorkerShiftsByDay(shift1.getWorkerName(), "2022-01-02").size());
  }

  @Test
  public void shouldNotAddWorkerShift() throws WorkerShiftException {
    WorkerShift shift =
        WorkerShift.builder()
            .shift(Shift.FIRST)
            .shiftDay("2022-01-01")
            .workerName("Worker1")
            .build();

    workerRepository.addWorkerShift(shift);

    assertEquals(1, workerRepository.getWorkerShifts(shift.getWorkerName()).size());

    WorkerShift shift1 =
        WorkerShift.builder()
            .shift(Shift.SECOND)
            .shiftDay("2022-01-01")
            .workerName("Worker1")
            .build();

    assertThrows(WorkerShiftException.class, () -> workerRepository.addWorkerShift(shift1));

    assertEquals(1, workerRepository.getWorkerShifts(shift.getWorkerName()).size());
    assertEquals(
        1, workerRepository.getWorkerShiftsByMonth(shift.getWorkerName(), "2022-01").size());
  }
}
