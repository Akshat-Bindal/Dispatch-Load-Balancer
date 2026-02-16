package com.akshat.dispatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class VehicleUpsertRequest {

    @NotBlank
    private String vehicleId;

    @Positive
    private double capacity;

    @JsonProperty("currentLatitude")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private double lat;

    @JsonProperty("currentLongitude")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private double lon;

    @NotBlank
    private String currentAddress;

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = capacity; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public String getCurrentAddress() { return currentAddress; }
    public void setCurrentAddress(String currentAddress) { this.currentAddress = currentAddress; }
}