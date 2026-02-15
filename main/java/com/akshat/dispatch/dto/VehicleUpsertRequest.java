package com.akshat.dispatch.dto;

import jakarta.validation.constraints.*;

public class VehicleUpsertRequest {

    @NotBlank
    private String vehicleId;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private double lat;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private double lon;

    @Positive
    private double capacity;

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = capacity; }
}