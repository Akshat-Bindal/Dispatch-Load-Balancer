package com.akshat.dispatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class VehiclePlanDto {
    private String vehicleId;

    private double capacity;

    @JsonProperty("currentLatitude")
    private double lat;

    @JsonProperty("currentLongitude")
    private double lon;

    private String currentAddress;

    private double totalLoad;
    private double totalDistanceKm;

    private List<OrderDto> assignedOrders = new ArrayList<>();

    public VehiclePlanDto() {}

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

    public double getTotalLoad() { return totalLoad; }
    public void setTotalLoad(double totalLoad) { this.totalLoad = totalLoad; }

    public double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public List<OrderDto> getAssignedOrders() { return assignedOrders; }
    public void setAssignedOrders(List<OrderDto> assignedOrders) { this.assignedOrders = assignedOrders; }
}