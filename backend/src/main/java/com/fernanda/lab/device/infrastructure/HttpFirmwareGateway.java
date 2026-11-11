package com.fernanda.lab.device.infrastructure;

import com.fernanda.lab.device.domain.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Component
public class HttpFirmwareGateway implements FirmwareGateway {
  private final RestClient client;

  public HttpFirmwareGateway(@Value("${firmware.base-url}") String baseUrl) {
    this.client = RestClient.builder().baseUrl(baseUrl).build();
  }

  public void apply(Fridge f) {
    client.post().uri("/apply").contentType(MediaType.APPLICATION_JSON)
        .body("{\"id\":" + f.id() + ",\"temperatureC\":" + f.temperatureC()
            + ",\"mode\":\"" + f.mode() + "\"}")
        .retrieve().toBodilessEntity();
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> state() {
    Map<String, Object> result = client.get().uri("/state").retrieve().body(Map.class);
    return result == null ? Map.of() : result;
  }
}
