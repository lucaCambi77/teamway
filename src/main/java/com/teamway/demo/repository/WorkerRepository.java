package com.teamway.demo.repository;

import com.teamway.demo.domain.WorkerShift;
import com.teamway.demo.exception.WorkerShiftException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkerRepository {

  private final Map<String, List<WorkerShift>> workerShiftMap = new HashMap<>();

  public List<WorkerShift> getShiftsByDay(String yearMonthDay) {
    return workerShiftMap.values().stream()
        .filter(l -> l.stream().allMatch(w -> w.getShiftDay().equals(yearMonthDay)))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public List<WorkerShift> getShiftsByMonth(String yearMonth) {
    return workerShiftMap.values().stream()
        .filter(l -> l.stream().allMatch(w -> w.getShiftDay().startsWith(yearMonth)))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public List<WorkerShift> getWorkerShifts(String name) {
    return workerShiftMap.get(name);
  }

  public List<WorkerShift> getWorkerShiftsByMonth(String name, String yearMonth) {
    return Optional.ofNullable(workerShiftMap.get(name))
        .map(
            l ->
                l.stream()
                    .filter(w -> w.getShiftDay().startsWith(yearMonth))
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public List<WorkerShift> getWorkerShiftsByDay(String name, String yearMonthDay) {
    return Optional.ofNullable(workerShiftMap.get(name))
        .map(
            l ->
                l.stream()
                    .filter(w -> w.getShiftDay().equals(yearMonthDay))
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public WorkerShift addWorkerShift(WorkerShift shift) throws WorkerShiftException {
    List<WorkerShift> workerShiftList =
        workerShiftMap.getOrDefault(shift.getWorkerName(), new ArrayList<>());

    if (workerShiftList.contains(shift))
      throw new WorkerShiftException(
          String.format("Worker %s has already a shift for required day", shift.getWorkerName()));

    workerShiftList.add(shift);

    workerShiftMap.put(shift.getWorkerName(), workerShiftList);

    return shift;
  }

  public WorkerShift removeWorkerShift(WorkerShift shift) {
    List<WorkerShift> workerShiftList =
        workerShiftMap.getOrDefault(shift.getWorkerName(), new ArrayList<>());

    workerShiftList.remove(shift);

    workerShiftMap.put(shift.getWorkerName(), workerShiftList);

    return shift;
  }
}
