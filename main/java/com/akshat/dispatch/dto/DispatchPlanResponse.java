package com.akshat.dispatch.dto;

import java.util.ArrayList;
import java.util.List;

public class DispatchPlanResponse {

    private List<VehiclePlanDto> vehicles = new ArrayList<>();
    private List<UnassignedOrderDto> unassignedOrders = new ArrayList<>();
    private Summary summary = new Summary();

    public List<VehiclePlanDto> getVehicles() { return vehicles; }
    public void setVehicles(List<VehiclePlanDto> vehicles) { this.vehicles = vehicles; }

    public List<UnassignedOrderDto> getUnassignedOrders() { return unassignedOrders; }
    public void setUnassignedOrders(List<UnassignedOrderDto> unassignedOrders) { this.unassignedOrders = unassignedOrders; }

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }

    public static class Summary {
        private int totalOrders;
        private int assignedOrders;
        private int unassignedOrders;
        private double totalDistanceKm;

        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

        public int getAssignedOrders() { return assignedOrders; }
        public void setAssignedOrders(int assignedOrders) { this.assignedOrders = assignedOrders; }

        public int getUnassignedOrders() { return unassignedOrders; }
        public void setUnassignedOrders(int unassignedOrders) { this.unassignedOrders = unassignedOrders; }

        public double getTotalDistanceKm() { return totalDistanceKm; }
        public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }
    }
}