package com.example.event.kafka;

import com.example.event.dto.ShipmentStatusEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentTrackingKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentTrackingKafkaConsumer.class);

    private static final String PROCESS_KEY = "shipment-tracking-process";
    private static final String MESSAGE_NAME = "Msg_ShipmentStatusUpdated";

    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper;

    public ShipmentTrackingKafkaConsumer(RuntimeService runtimeService, ObjectMapper objectMapper) {
        this.runtimeService = runtimeService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "shipment.status.updated", groupId = "camunda-message-start-event")
    public void handleShipmentStatusEvent(String payload) {
        ShipmentStatusEvent event;
        try {
            event = objectMapper.readValue(payload, ShipmentStatusEvent.class);
        } catch (Exception e) {
            LOG.error("Failed to deserialize shipment status event: {}", payload, e);
            return;
        }
        String businessKey = "TRACK-" + event.getCarrier().toUpperCase() + "-" + event.getTrackingNumber();
        LOG.info("Kafka event received: Shipment [{}] status [{}] from carrier [{}]", event.getTrackingNumber(), event.getStatus(), event.getCarrier());

        ProcessInstance existing = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(PROCESS_KEY)
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        if (existing != null) {
            LOG.warn("Shipment tracking process already running for business key: {} (instance: {}). Skipping duplicate.", businessKey, existing.getId());
            return;
        }

        runtimeService.createMessageCorrelation(MESSAGE_NAME)
                .processInstanceBusinessKey(businessKey)
                .setVariable("trackingNumber", event.getTrackingNumber())
                .setVariable("carrier", event.getCarrier())
                .setVariable("status", event.getStatus())
                .setVariable("orderId", event.getOrderId())
                .correlate();

        LOG.info("Shipment tracking process started with business key: {}", businessKey);
    }
}
