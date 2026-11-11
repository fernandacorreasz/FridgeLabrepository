package com.fernanda.lab.device.domain;
import java.util.List;
import java.util.Optional;
public interface FridgeRepository {
  List<Fridge> find(String name, String owner);
  Optional<Fridge> byId(long id, String owner);
  Fridge insert(Fridge fridge, String owner);
  void update(Fridge fridge, String owner);
  void saveConfiguration(Fridge fridge, String owner);
  void updateFirmwareCode(long id, String code, String owner);
  void delete(long id, String owner);
}
