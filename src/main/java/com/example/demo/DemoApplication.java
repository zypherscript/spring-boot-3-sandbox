package com.example.demo;

import com.example.demo.entity.Customer;
import com.example.demo.model.Activity;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
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

//https://www.boredapi.com/api/
interface BoredClient {

  @GetExchange("/activity")
  Activity suggestAnActivity();
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

  @Around("execution(* com.example.demo.controller.*.test*(String))")
  public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long elapsedTime = System.currentTimeMillis() - start;
    System.out.println(joinPoint.getSignature().getName() + " executed in " + elapsedTime + "ms");
    return result;
  }
}

