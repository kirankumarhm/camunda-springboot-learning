package com.example.event.dto;
/**
 * Order request DTO.
 */
public record OrderRequest(String orderId, String customerId, Double totalAmount, String items) {}
