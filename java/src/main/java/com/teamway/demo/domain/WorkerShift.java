package com.teamway.demo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Builder
@Data
@EqualsAndHashCode(of = { "workerName", "shiftDay" })
public class WorkerShift {

    private String workerName;
    private String shiftDay;
    private Shift shift;
}
