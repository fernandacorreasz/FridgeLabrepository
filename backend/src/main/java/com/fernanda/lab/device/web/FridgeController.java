package com.fernanda.lab.device.web;

import com.fernanda.lab.device.application.ConfigureFridge;
import com.fernanda.lab.device.domain.Fridge;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins={"http://localhost:3002"})
@SecurityRequirement(name="basicAuth")
@Tag(name="Fridges")
public class FridgeController {
  private final ConfigureFridge service;
  public FridgeController(ConfigureFridge service) { this.service = service; }

  public record Settings(@NotNull @Min(-20) @Max(15) Integer temperatureC,
      @NotBlank String mode, @NotNull @Size(max=250) String note) {}

  public record DeviceRequest(@NotBlank @Size(max=100) String name,
      @NotNull @Min(-20) @Max(15) Integer temperatureC, @NotBlank String mode,
      @NotNull @Size(max=250) String note, @NotBlank @Size(max=80) String country,
      @NotBlank @Size(max=120) String city, @NotNull @DecimalMin("-90") @DecimalMax("90") Double latitude,
      @NotNull @DecimalMin("-180") @DecimalMax("180") Double longitude,
      @NotNull @Size(max=12000) String firmwareCode) {}

  public record FirmwareCode(@NotNull @Size(max=12000) String code) {}

  @Operation(summary="Listar dispositivos do usuário autenticado")
  @GetMapping("/devices")
  public List<Fridge> list(@RequestParam(defaultValue="") String name, Authentication auth) { return service.list(name, auth.getName()); }

  @GetMapping("/devices/{id}")
  public Fridge get(@PathVariable long id, Authentication auth) { return service.get(id, auth.getName()); }

  @PostMapping({"/devices", "/devices/register"})
  @ResponseStatus(HttpStatus.CREATED)
  public Fridge create(@RequestBody @Valid DeviceRequest request, Authentication auth) {
    return service.create(request.name(), request.temperatureC(), request.mode(), request.note(), request.country(), request.city(), request.latitude(), request.longitude(), request.firmwareCode(), auth.getName());
  }

  @PutMapping("/devices/{id}")
  public Fridge update(@PathVariable long id, @RequestBody @Valid DeviceRequest request, Authentication auth) {
    return service.update(id, request.name(), request.temperatureC(), request.mode(), request.note(), request.country(), request.city(), request.latitude(), request.longitude(), request.firmwareCode(), auth.getName());
  }

  @DeleteMapping("/devices/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id, Authentication auth) { service.delete(id, auth.getName()); }

  @PutMapping("/devices/{id}/configuration")
  public Fridge configure(@PathVariable long id, @RequestBody @Valid Settings settings, Authentication auth) {
    return service.updateConfiguration(id, settings.temperatureC(), settings.mode(), settings.note(), auth.getName());
  }

  @PutMapping("/devices/{id}/firmware")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void firmware(@PathVariable long id, @RequestBody @Valid FirmwareCode code, Authentication auth) {
    service.updateFirmwareCode(id, code.code(), auth.getName());
  }

  @GetMapping("/firmware/state")
  public Map<String,Object> state(Authentication auth) { return service.firmwareState(auth.getName()); }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,String>> invalid(IllegalArgumentException e) {
    HttpStatus status = "Dispositivo não encontrado".equals(e.getMessage()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
  }
}
