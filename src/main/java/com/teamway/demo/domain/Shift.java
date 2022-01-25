package com.teamway.demo.domain;

public enum Shift {
  FIRST("0 - 8 working shift"),
  SECOND("8 - 16 working shift"),
  THIRD("16 - 24 working shift");

  private final String description;

  Shift(String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }
}
