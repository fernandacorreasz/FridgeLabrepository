package com.fernanda.lab;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean OpenAPI fridgeOpenAPI() {
    return new OpenAPI().info(new Info().title("Fridge Lab API").version("1.0.0")
        .description("API didática para dispositivos fictícios; use somente em laboratório local."))
        .schemaRequirement("basicAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"));
  }
}
