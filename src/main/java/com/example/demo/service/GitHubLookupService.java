package com.example.demo.service;

import com.example.demo.model.User;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubLookupService {

  private final RestTemplate restTemplate;

  @Async
  public CompletableFuture<User> findUser(String user) throws InterruptedException {
    log.info("Looking up " + user);
    var url = String.format("https://api.github.com/users/%s", user);
    var results = restTemplate.getForObject(url, User.class);
    // Artificial delay of 1s for demonstration purposes
    Thread.sleep(1000L);
    return CompletableFuture.completedFuture(results);
  }
}
