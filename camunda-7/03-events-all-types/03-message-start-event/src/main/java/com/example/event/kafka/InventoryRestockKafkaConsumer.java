package com.example.event.kafka;

import com.example.event.dto.InventoryRestockEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryRestockKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryRestockKafkaConsumer.class);

    private static final String PROCESS_KEY = "inventory-restock-process";
    private static final String MESSAGE_NAME = "Msg_InventoryRestockRequested";

    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper;

    public InventoryRestockKafkaConsumer(RuntimeService runtimeService, ObjectMapper objectMapper) {
        this.runtimeService = runtimeService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "inventory.restock.requested", groupId = "camunda-message-start-event")
    public void handleRestockEvent(String payload) {
        InventoryRestockEvent event;
        try {
            event = objectMapper.readValue(payload, InventoryRestockEvent.class);
        } catch (Exception e) {
            LOG.error("Failed to deserialize restock event: {}", payload, e);
            return;
        }
        String businessKey = "RESTOCK-" + event.getWarehouseId() + "-" + event.getProductId();
        LOG.info("Kafka event received: Restock requested for product [{}] in warehouse [{}]", event.getProductId(), event.getWarehouseId());

        ProcessInstance existing = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(PROCESS_KEY)
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        if (existing != null) {
            LOG.warn("Restock process already running for business key: {} (instance: {}). Skipping duplicate.", businessKey, existing.getId());
            return;
        }

        runtimeService.createMessageCorrelation(MESSAGE_NAME)
                .processInstanceBusinessKey(businessKey)
                .setVariable("productId", event.getProductId())
                .setVariable("warehouseId", event.getWarehouseId())
                .setVariable("currentStock", event.getCurrentStock())
                .setVariable("reorderQuantity", event.getReorderQuantity())
                .correlate();

        LOG.info("Restock process started with business key: {}", businessKey);
    }
}
