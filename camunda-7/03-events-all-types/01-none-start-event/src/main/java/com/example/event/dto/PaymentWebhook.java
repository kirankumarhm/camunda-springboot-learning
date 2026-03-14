package com.example.event.dto;

/**
     * Payment webhook DTO.
     */
    public record PaymentWebhook(String transactionId, Double amount, String currency, 
                                  String paymentMethod, String customerId) {}
