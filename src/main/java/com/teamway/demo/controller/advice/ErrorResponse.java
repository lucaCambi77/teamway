package com.teamway.demo.controller.advice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class ErrorResponse {

  private Date timestamp;
  private Integer status;
  private String error;
  private String message;
}
