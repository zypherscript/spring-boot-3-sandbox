package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
class CustomerController {

  private final CustomerRepository customerRepository;

  @ResponseBody
  @GetMapping("/test/customers")
  Iterable<Customer> customers() {
    return customerRepository.findAll();
  }

  @QueryMapping
    //graphql
  Collection<Customer> customersByName(@Argument String name) {
    return customerRepository.findByName(name);
  }

  @SneakyThrows
  @GetMapping("/test/{str}")
  ResponseEntity<Void> test(@PathVariable String str) {
    TimeUnit.SECONDS.sleep(2);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}