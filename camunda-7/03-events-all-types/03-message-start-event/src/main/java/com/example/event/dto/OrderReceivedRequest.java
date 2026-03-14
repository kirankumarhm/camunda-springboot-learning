package com.example.event.dto;

public class OrderReceivedRequest {

    private String orderId;
    private String customerName;
    private String channel;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
