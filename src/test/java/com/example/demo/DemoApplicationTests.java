package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.Customer;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

  @Autowired
  TestRestTemplate restTemplate;

  @Test
  void contextLoads() throws URISyntaxException {
    HttpHeaders headers = createCsrfHeaders();
    RequestEntity<Void> request = new RequestEntity<>(headers,
        HttpMethod.POST,
        new URI("/request/test/customers"));
    ParameterizedTypeReference<Iterable<Customer>> responseType = new ParameterizedTypeReference<>() {
    };

    ResponseEntity<Iterable<Customer>> response = restTemplate
        .withBasicAuth("user", "password")
        .exchange("/request/test/customers",
            HttpMethod.POST, request,
            responseType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /**
   * This method creates the CSRF headers like a Javascript client would, by reading the cookie and
   * then sending the value back in both the cookie and the header (the Double-Submit Cookie
   * pattern).
   */
  private HttpHeaders createCsrfHeaders() {
    ResponseEntity<Void> response = restTemplate.withBasicAuth("user", "password")
        .exchange("/request", HttpMethod.HEAD, null, Void.class);
    HttpCookie cookie = HttpCookie.parse(
            Objects.requireNonNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)))
        .stream()
        .filter(c -> c.getName().equals("XSRF-TOKEN"))
        .findFirst()
        .get();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.COOKIE, cookie.toString());
    headers.add("X-XSRF-TOKEN", cookie.getValue());
    return headers;
  }

}
