package com.fernanda.lab.device.infrastructure;

import com.fernanda.lab.device.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcFridgeRepository implements FridgeRepository {
  private final JdbcTemplate jdbc;

  public JdbcFridgeRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private final org.springframework.jdbc.core.RowMapper<Fridge> mapper = (rs, n) -> new Fridge(rs.getLong("id"),
      rs.getString("name"), rs.getInt("temperature_c"), rs.getString("mode"), rs.getString("note"),
      rs.getString("country"), rs.getString("city"), rs.getDouble("latitude"), rs.getDouble("longitude"),
      rs.getString("firmware_code"));
  private static final String COLUMNS = "id,name,temperature_c,mode,note,country,city,latitude,longitude,firmware_code";

  public List<Fridge> find(String name, String owner) {
    String term = name == null ? "" : name;
    return jdbc.query("SELECT " + COLUMNS + " FROM fridge_devices WHERE owner_id=? AND name ILIKE ? ORDER BY id",
        mapper, owner, "%" + term + "%");
  }

  public Optional<Fridge> byId(long id, String owner) {
    return jdbc.query("SELECT " + COLUMNS + " FROM fridge_devices WHERE id=? AND owner_id=?", mapper, id, owner)
        .stream().findFirst();
  }

  public Fridge insert(Fridge f, String owner) {
    Long id = jdbc.queryForObject("SELECT COALESCE(MAX(id),0)+1 FROM fridge_devices", Long.class);
    jdbc.update(
        "INSERT INTO fridge_devices(id,name,temperature_c,mode,note,owner_id,country,city,latitude,longitude,firmware_code) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
        id, f.name(), f.temperatureC(), f.mode(), f.note(), owner, f.country(), f.city(), f.latitude(), f.longitude(),
        f.firmwareCode());
    return new Fridge(id, f.name(), f.temperatureC(), f.mode(), f.note(), f.country(), f.city(), f.latitude(),
        f.longitude(), f.firmwareCode());
  }

  public void update(Fridge f, String owner) {
    int changed = jdbc.update(
        "UPDATE fridge_devices SET name=?,temperature_c=?,mode=?,note=?,country=?,city=?,latitude=?,longitude=?,firmware_code=?,updated_at=now() WHERE id=? AND owner_id=?",
        f.name(), f.temperatureC(), f.mode(), f.note(), f.country(), f.city(), f.latitude(), f.longitude(),
        f.firmwareCode(), f.id(), owner);
    if (changed != 1)
      throw new IllegalArgumentException("Dispositivo não encontrado");
  }

  public void saveConfiguration(Fridge f, String owner) {
    int changed = jdbc.update(
        "UPDATE fridge_devices SET temperature_c=?,mode=?,note=?,updated_at=now() WHERE id=? AND owner_id=?",
        f.temperatureC(), f.mode(), f.note(), f.id(), owner);
    if (changed != 1)
      throw new IllegalArgumentException("Dispositivo não encontrado");
  }

  public void updateFirmwareCode(long id, String code, String owner) {
    if (jdbc.update("UPDATE fridge_devices SET firmware_code=?,updated_at=now() WHERE id=? AND owner_id=?", code, id,
        owner) != 1)
      throw new IllegalArgumentException("Dispositivo não encontrado");
  }

  public void delete(long id, String owner) {
    if (jdbc.update("DELETE FROM fridge_devices WHERE id=? AND owner_id=?", id, owner) != 1)
      throw new IllegalArgumentException("Dispositivo não encontrado");
  }
}
