package com.example.demo.config;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.CACHE;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

@Configuration
@EnableWebSecurity
@Profile("!vaadin-sec-view")
public class WebSecurity {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrfConfigurer -> csrfConfigurer
            .csrfTokenRepository(
                CookieCsrfTokenRepository.withHttpOnlyFalse()) //lot of thing to learn like request(post) with xsrf-token...
            .ignoringRequestMatchers("/graphql")
        )
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .logout(logout ->
                //normally csrf doesn't permit us to log out with GET but something went wrong when using with vaadin
                //need to create new demoing for learning more about csrf
                logout
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //anyway no need for this demo(with vaadin, we can)
                    .addLogoutHandler(
                        new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(CACHE, COOKIES)))
        );
    return http.build();
  }

  @Bean
  DelegatingPasswordEncoder passwordEncoder() {
    var bcrypt = new BCryptPasswordEncoder(13);
    Map<String, PasswordEncoder> passwordEncoders = Map.of("bcrypt", bcrypt);
    return new DelegatingPasswordEncoder("bcrypt", passwordEncoders);
  }

  @Bean
  UserDetailsService users() {
    return new InMemoryUserDetailsManager(
        User.withUsername("user")
            .password("{bcrypt}$2a$13$3fFG6h/SKI19kzsnO8v.hu5vEwIwmqUyV5YzEJS7HG3thx0OkHMjm")
            .roles("USER")
            .build()
    );
  }

}
