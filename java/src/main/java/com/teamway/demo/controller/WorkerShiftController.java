package com.teamway.demo.controller;

import com.teamway.demo.domain.WorkerShift;
import com.teamway.demo.exception.WorkerShiftException;
import com.teamway.demo.repository.WorkerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workerShift")
@RequiredArgsConstructor
public class WorkerShiftController {

  private final WorkerRepository workerRepository = new WorkerRepository();

  @PostMapping(
      produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(code = HttpStatus.CREATED)
  public WorkerShift addWorkerShift(@RequestBody WorkerShift shift) throws WorkerShiftException {
    return workerRepository.addWorkerShift(shift);
  }
}
