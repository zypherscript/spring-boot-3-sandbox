package com.example.demo.config;

import com.example.demo.vaadin.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@Configuration
@Profile("vaadin-sec-view")
public class SecurityConfig extends VaadinWebSecurity {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf(csrfConfigurer -> csrfConfigurer.ignoringRequestMatchers("/graphql"))
        .authorizeHttpRequests(authorize ->
            authorize.requestMatchers("/actuator/**").permitAll());
    super.configure(http);
    setLoginView(http, LoginView.class);
  }

  @Bean
  public UserDetailsService users() {
    var admin = User.withUsername("admin")
        .password("{noop}p@ssw0rd")
        .roles("USER", "ADMIN")
        .build();
    return new InMemoryUserDetailsManager(admin);
  }

}
