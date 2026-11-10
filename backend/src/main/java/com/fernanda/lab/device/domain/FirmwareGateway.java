package com.fernanda.lab.device.domain;
import java.util.Map;
public interface FirmwareGateway {
  void apply(Fridge fridge);
  Map<String,Object> state();
}
