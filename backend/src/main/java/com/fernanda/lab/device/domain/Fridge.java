package com.fernanda.lab.device.domain;
public record Fridge(long id, String name, int temperatureC, String mode, String note) {
  public Fridge configure(int target, String newMode, String newNote) {
    if (target < -20 || target > 15) throw new IllegalArgumentException("Temperatura fora do intervalo");
    if (!newMode.equals("ECO") && !newMode.equals("NORMAL") && !newMode.equals("TURBO")) throw new IllegalArgumentException("Modo inválido");
    if (newNote.length() > 250) throw new IllegalArgumentException("Nota muito longa");
    return new Fridge(id, name, target, newMode, newNote);
  }
}
