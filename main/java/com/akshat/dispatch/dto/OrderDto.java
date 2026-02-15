package com.akshat.dispatch.dto;

import com.akshat.dispatch.model.Priority;

public class OrderDto {
    private String orderId;
    private double lat;
    private double lon;
    private double weight;
    private Priority priority;

    public OrderDto() {}

    public OrderDto(String orderId, double lat, double lon, double weight, Priority priority) {
        this.orderId = orderId;
        this.lat = lat;
        this.lon = lon;
        this.weight = weight;
        this.priority = priority;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}