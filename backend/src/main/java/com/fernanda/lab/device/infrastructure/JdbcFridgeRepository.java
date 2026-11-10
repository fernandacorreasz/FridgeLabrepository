package com.fernanda.lab.device.infrastructure;

import com.fernanda.lab.device.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository public class JdbcFridgeRepository implements FridgeRepository {
  private final JdbcTemplate jdbc;
  public JdbcFridgeRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
  private final org.springframework.jdbc.core.RowMapper<Fridge> mapper = (rs, n) ->
      new Fridge(rs.getLong("id"), rs.getString("name"), rs.getInt("temperature_c"),
          rs.getString("mode"), rs.getString("note"));

  public List<Fridge> find(String name, String owner) {
    String term = name == null ? "" : name;
    return jdbc.query("SELECT id,name,temperature_c,mode,note FROM fridge_devices WHERE owner_id=? AND name ILIKE ? ORDER BY id",
        mapper, owner, "%" + term + "%");
  }
  public Optional<Fridge> byId(long id, String owner) {
    return jdbc.query("SELECT id,name,temperature_c,mode,note FROM fridge_devices WHERE id=? AND owner_id=?",
        mapper, id, owner).stream().findFirst();
  }
  public void save(Fridge f, String owner) {
    int changed = jdbc.update("UPDATE fridge_devices SET temperature_c=?,mode=?,note=?,updated_at=now() WHERE id=? AND owner_id=?",
        f.temperatureC(), f.mode(), f.note(), f.id(), owner);
    if (changed != 1) throw new IllegalArgumentException("Dispositivo não encontrado");
  }
}
