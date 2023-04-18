package com.example.demo;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  BoredClient boredClient(WebClient.Builder builder) {
    return HttpServiceProxyFactory
        .builder(
            WebClientAdapter.forClient(builder.baseUrl("https://www.boredapi.com/api/").build()))
        .build()
        .createClient(BoredClient.class);
  }

  @Bean
  ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener() {
    return event -> System.out.println(event.getResolvableType() + " :: " + event.getState());
  }
}

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

//https://www.boredapi.com/api/
interface BoredClient {

  @GetExchange("/activity")
  Activity suggestAnActivity();
}

record Activity(String activity, int participants) {

}

@Controller
@RequiredArgsConstructor
class BoredActivityController {

  private final BoredClient client;

  @SchemaMapping(typeName = "Customer")
  Activity suggestedActivity(Customer customer) {
    return this.client.suggestAnActivity();
  }
}

@Aspect
@Component
class MyAspect {

  @Around("execution(* com.example.demo.*.test*(String))")
  public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long elapsedTime = System.currentTimeMillis() - start;
    System.out.println(joinPoint.getSignature().getName() + " executed in " + elapsedTime + "ms");
    return result;
  }
}

