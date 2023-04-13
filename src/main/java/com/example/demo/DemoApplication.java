package com.example.demo;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
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

}

record Customer(@Id Integer id, String name) {

}

interface CustomerRepository extends CrudRepository<Customer, Integer> {

  Collection<Customer> findByName(String name);
}

@Controller
@RequiredArgsConstructor
class CustomerController {

  private final CustomerRepository customerRepository;

  @ResponseBody
  @GetMapping("/customers")
  Iterable<Customer> customers() {
    return customerRepository.findAll();
  }

  @QueryMapping
    //graphql
  Collection<Customer> customersByName(@Argument String name) {
    return customerRepository.findByName(name);
  }

  @GetMapping("/testSome/{str}")
  void callTestSome(@PathVariable String str) {
    testSome(str);
  }

  @SneakyThrows
  void testSome(String str) {
    TimeUnit.SECONDS.sleep(2);
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

@Route("listCustomers")
class WebsiteView extends VerticalLayout {

  final Grid<Customer> grid;

  private final CustomerRepository customerRepository;

  public WebsiteView(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;

    grid = new Grid<>();
    grid.addColumn(Customer::id).setHeader("ID");
    grid.addColumn(Customer::name).setHeader("Name");

    add(grid);

    listCustomers();
  }

  private void listCustomers() {
    var customers = StreamSupport.stream(customerRepository.findAll()
        .spliterator(), false).toList();
    grid.setItems(customers);
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

