package com.fernanda.lab.device.domain;

public record Fridge(long id, String name, int temperatureC, String mode, String note, String country, String city,
    double latitude, double longitude, String firmwareCode) {
  public Fridge configure(int target, String newMode, String newNote) {
    validateConfiguration(target, newMode, newNote);
    return new Fridge(id, name, target, newMode, newNote, country, city, latitude, longitude, firmwareCode);
  }

  public Fridge withDetails(String newName, int target, String newMode, String newNote, String newCountry,
      String newCity, double newLatitude, double newLongitude, String newFirmwareCode) {
    validateConfiguration(target, newMode, newNote);
    if (newName == null || newName.isBlank() || newName.length() > 100)
      throw new IllegalArgumentException("Nome inválido");
    if (newCountry == null || newCountry.isBlank() || newCountry.length() > 80)
      throw new IllegalArgumentException("País inválido");
    if (newCity == null || newCity.isBlank() || newCity.length() > 120)
      throw new IllegalArgumentException("Cidade inválida");
    if (newLatitude < -90 || newLatitude > 90 || newLongitude < -180 || newLongitude > 180)
      throw new IllegalArgumentException("Coordenadas inválidas");
    if (newFirmwareCode == null || newFirmwareCode.length() > 12000)
      throw new IllegalArgumentException("Código de firmware muito longo");
    return new Fridge(id, newName.trim(), target, newMode, newNote, newCountry.trim(), newCity.trim(), newLatitude,
        newLongitude, newFirmwareCode);
  }

  private static void validateConfiguration(int target, String newMode, String newNote) {
    if (target < -20 || target > 15)
      throw new IllegalArgumentException("Temperatura fora do intervalo");
    if (!"ECO".equals(newMode) && !"NORMAL".equals(newMode) && !"TURBO".equals(newMode))
      throw new IllegalArgumentException("Modo inválido");
    if (newNote == null || newNote.length() > 250)
      throw new IllegalArgumentException("Nota inválida ou muito longa");
  }
}
