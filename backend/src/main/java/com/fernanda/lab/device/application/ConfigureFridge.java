package com.fernanda.lab.device.application;

import com.fernanda.lab.device.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service public class ConfigureFridge {
  private final FridgeRepository repository;
  private final FirmwareGateway firmware;
  public ConfigureFridge(FridgeRepository repository, FirmwareGateway firmware) { this.repository = repository; this.firmware = firmware; }
  public List<Fridge> list(String search, String owner) { return repository.find(search, owner); }
  public Fridge get(long id, String owner) { return repository.byId(id, owner).orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado")); }
  public Fridge update(long id, int temp, String mode, String note, String owner) {
    Fridge fridge = get(id, owner).configure(temp, mode, note);
    // Apply externally first. A failed firmware call leaves the database unchanged.
    firmware.apply(fridge);
    repository.save(fridge, owner);
    return fridge;
  }
  public Map<String,Object> firmwareState(String owner) {
    Map<String,Object> raw = firmware.state();
    Object devices = raw.get("devices");
    if (!(devices instanceof Map<?, ?> all)) return Map.of("devices", Map.of());
    Map<String,Object> allowed = new java.util.HashMap<>();
    for (Fridge fridge : repository.find("", owner)) {
      Object state = all.get(String.valueOf(fridge.id()));
      if (state != null) allowed.put(String.valueOf(fridge.id()), state);
    }
    return Map.of("devices", allowed);
  }
}
