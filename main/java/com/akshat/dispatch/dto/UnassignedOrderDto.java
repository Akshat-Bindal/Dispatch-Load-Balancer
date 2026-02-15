package com.akshat.dispatch.dto;

public class UnassignedOrderDto {
    private String orderId;
    private String reason;

    public UnassignedOrderDto() {}

    public UnassignedOrderDto(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}