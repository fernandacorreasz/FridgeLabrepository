package com.fernanda.lab.device.domain;
import java.util.List;
import java.util.Optional;
public interface FridgeRepository {
  List<Fridge> find(String name, String owner);
  Optional<Fridge> byId(long id, String owner);
  void save(Fridge fridge, String owner);
}
