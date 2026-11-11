package com.fernanda.lab.device.application;

import com.fernanda.lab.device.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ConfigureFridge {
  private final FridgeRepository repository;
  private final FirmwareGateway firmware;

  public ConfigureFridge(FridgeRepository repository, FirmwareGateway firmware) {
    this.repository = repository;
    this.firmware = firmware;
  }

  public List<Fridge> list(String search, String owner) {
    return repository.find(search, owner);
  }

  public Fridge get(long id, String owner) {
    return repository.byId(id, owner).orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado"));
  }

  public Fridge create(String name, int temp, String mode, String note, String country, String city, double latitude,
      double longitude, String firmwareCode, String owner) {
    Fridge fridge = new Fridge(0, "novo", 0, "ECO", "", "Brasil", "São Paulo", 0, 0, "").withDetails(name, temp, mode,
        note, country, city, latitude, longitude, firmwareCode);
    return repository.insert(fridge, owner);
  }

  public Fridge update(long id, String name, int temp, String mode, String note, String country, String city,
      double latitude, double longitude, String firmwareCode, String owner) {
    Fridge updated = get(id, owner).withDetails(name, temp, mode, note, country, city, latitude, longitude,
        firmwareCode);
    repository.update(updated, owner);
    return updated;
  }

  public Fridge updateConfiguration(long id, int temp, String mode, String note, String owner) {
    Fridge fridge = get(id, owner).configure(temp, mode, note);
    firmware.apply(fridge);
    repository.saveConfiguration(fridge, owner);
    return fridge;
  }

  public void updateFirmwareCode(long id, String code, String owner) {
    get(id, owner);
    repository.updateFirmwareCode(id, code, owner);
  }

  public void delete(long id, String owner) {
    get(id, owner);
    repository.delete(id, owner);
  }

  public Map<String, Object> firmwareState(String owner) {
    Map<String, Object> raw = firmware.state();
    Object devices = raw.get("devices");
    if (!(devices instanceof Map<?, ?> all))
      return Map.of("devices", Map.of());
    java.util.Map<String, Object> allowed = new java.util.HashMap<>();
    for (Fridge fridge : repository.find("", owner)) {
      Object state = all.get(String.valueOf(fridge.id()));
      if (state != null)
        allowed.put(String.valueOf(fridge.id()), state);
    }
    return Map.of("devices", allowed);
  }
}
