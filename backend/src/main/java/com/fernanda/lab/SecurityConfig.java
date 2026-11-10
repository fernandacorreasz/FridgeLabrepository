package com.fernanda.lab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  InMemoryUserDetailsManager users() {
    UserDetails teacher = User.withUsername("teacher")
        .password("{noop}laboratorio-teacher")
        .roles("USER")
        .build();
    UserDetails student = User.withUsername("student")
        .password("{noop}laboratorio-student")
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(teacher, student);
  }
}
