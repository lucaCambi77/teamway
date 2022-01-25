package com.teamway.demo.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Builder
@Data
public class WorkerShift {

  private String workerName;
  private String shiftDay;
  private Shift shift;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WorkerShift that = (WorkerShift) o;
    return workerName.equals(that.workerName) && shiftDay.equals(that.shiftDay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workerName, shiftDay);
  }
}
