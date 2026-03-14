package com.example.event.dto;

/**
     * Document webhook DTO.
     */
    public record DocumentWebhook(String documentId, String documentType, 
                                   String uploadedBy, String fileUrl) {}
