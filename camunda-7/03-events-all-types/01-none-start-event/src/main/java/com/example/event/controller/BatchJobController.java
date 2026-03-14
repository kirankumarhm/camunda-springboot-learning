package com.example.event.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for admin batch job operations.
 * Handles batch processing tasks like invoice generation, reports, etc.
 */
@RestController
@RequestMapping("/api/admin")
public class BatchJobController {

    private final RuntimeService runtimeService;

    public BatchJobController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    /**
     * Triggers batch invoice generation for multiple customers.
     *
     * @param request Batch request with customer IDs and billing period
     * @return Batch processing status
     */
    @PostMapping("/batch/invoice-generation")
    public Map<String, Object> triggerInvoiceGeneration(@RequestBody BatchRequest request) {
        List<String> processIds = new ArrayList<>();
        
        for (String customerId : request.customerIds()) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerId", customerId);
            variables.put("billingPeriod", request.billingPeriod());
            variables.put("triggeredBy", "admin");
            variables.put("triggeredAt", LocalDateTime.now().toString());

            var instance = runtimeService.startProcessInstanceByKey(
                "batch-invoice-process",
                "BATCH-" + customerId + "-" + System.currentTimeMillis(),
                variables
            );
            processIds.add(instance.getId());
        }

        return Map.of(
            "batchId", UUID.randomUUID().toString(),
            "processCount", processIds.size(),
            "processInstanceIds", processIds,
            "status", "Batch job triggered successfully"
        );
    }

    /**
     * Batch request DTO.
     */
    public record BatchRequest(List<String> customerIds, String billingPeriod) {}
}
