package com.teamway.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamway.demo.DemoApplication;
import com.teamway.demo.domain.Shift;
import com.teamway.demo.domain.WorkerShift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WorkerShiftControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private WebApplicationContext context;

  private final MediaType mediaType = MediaType.APPLICATION_JSON;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  public void shouldCreateWorkerShift() throws Exception {

    WorkerShift shift =
        WorkerShift.builder()
            .shift(Shift.FIRST)
            .shiftDay("2022-01-01")
            .workerName("Worker1")
            .build();

    mockMvc
        .perform(
            post("/workerShift/")
                .contentType(mediaType)
                .content(new ObjectMapper().writeValueAsString(shift)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(mediaType))
        .andExpect(jsonPath("$.workerName", is(shift.getWorkerName())))
        .andExpect(jsonPath("$.shift", is(shift.getShift().name())));
  }

  @Test
  public void shouldThrowWhenWorkerShiftSameDay() throws Exception {

    WorkerShift shift =
        WorkerShift.builder()
            .shift(Shift.FIRST)
            .shiftDay("2022-01-01")
            .workerName("Worker1")
            .build();

    mockMvc
        .perform(
            post("/workerShift/")
                .contentType(mediaType)
                .content(new ObjectMapper().writeValueAsString(shift)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(mediaType))
        .andExpect(jsonPath("$.workerName", is(shift.getWorkerName())))
        .andExpect(jsonPath("$.shift", is(shift.getShift().name())));

    WorkerShift shift1 =
        WorkerShift.builder()
            .shift(Shift.FIRST)
            .shiftDay("2022-01-01")
            .workerName("Worker1")
            .build();

    mockMvc
        .perform(
            post("/workerShift/")
                .contentType(mediaType)
                .content(new ObjectMapper().writeValueAsString(shift1)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(mediaType));
  }
}
