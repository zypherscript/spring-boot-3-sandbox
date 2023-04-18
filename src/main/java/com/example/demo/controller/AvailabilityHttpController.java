package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AvailabilityHttpController {

  private final ApplicationContext context;

  @GetMapping("/down")
  void down() {
    AvailabilityChangeEvent.publish(this.context, LivenessState.BROKEN);
  }

  @SneakyThrows
  @GetMapping("/slow")
  void slow() {
    Thread.sleep(10_000);
  }

}
