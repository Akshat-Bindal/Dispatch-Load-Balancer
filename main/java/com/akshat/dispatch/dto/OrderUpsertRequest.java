package com.akshat.dispatch.dto;

import com.akshat.dispatch.model.Priority;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class OrderUpsertRequest {

    @NotBlank
    private String orderId;

    @JsonProperty("latitude")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private double lat;

    @JsonProperty("longitude")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private double lon;

    @NotBlank
    private String address;

    @JsonProperty("packageWeight")
    @Positive
    private double weight;

    @NotNull
    private Priority priority;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}