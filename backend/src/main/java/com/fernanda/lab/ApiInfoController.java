package com.fernanda.lab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiInfoController {
  @GetMapping({"/", "/api"})
  public Map<String, String> info() {
    return Map.of(
        "name", "Fridge Lab API",
        "status", "ok",
        "swaggerUi", "/swagger-ui/index.html",
        "openapi", "/v3/api-docs",
        "authenticatedApi", "Use Basic Auth em /api/devices e /api/firmware/state"
    );
  }
}
