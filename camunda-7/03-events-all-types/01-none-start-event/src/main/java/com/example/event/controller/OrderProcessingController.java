package com.example.event.controller;

import com.example.event.dto.OrderRequest;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for order processing operations.
 * Handles order creation and triggers order processing workflow.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderProcessingController {

    private final RuntimeService runtimeService;

    public OrderProcessingController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    /**
     * Creates a new order and starts the order processing workflow.
     *
     * @param request Order details
     * @return Process instance information
     */
    @PostMapping("/create")
    public Map<String, String> createOrder(@RequestBody OrderRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", request.orderId());
        variables.put("customerId", request.customerId());
        variables.put("totalAmount", request.totalAmount());
        variables.put("items", request.items());

        var instance = runtimeService.startProcessInstanceByKey(
            "order-processing-process",
            request.orderId(),
            variables
        );

        return Map.of(
            "processInstanceId", instance.getId(),
            "orderId", request.orderId(),
            "status", "Order processing started"
        );
    }


}
