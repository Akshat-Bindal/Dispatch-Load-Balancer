package com.akshat.dispatch.dto;

import java.util.ArrayList;
import java.util.List;

public class VehiclePlanDto {
    private String vehicleId;
    private double totalLoad;
    private double totalDistanceKm;
    private List<OrderDto> route = new ArrayList<>();

    public VehiclePlanDto() {}

    public VehiclePlanDto(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public double getTotalLoad() { return totalLoad; }
    public void setTotalLoad(double totalLoad) { this.totalLoad = totalLoad; }

    public double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public List<OrderDto> getRoute() { return route; }
    public void setRoute(List<OrderDto> route) { this.route = route; }
}