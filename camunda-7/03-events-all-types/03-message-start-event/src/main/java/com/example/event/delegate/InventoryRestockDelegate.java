package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("inventoryRestockDelegate")
public class InventoryRestockDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryRestockDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String productId = (String) execution.getVariable("productId");
        String warehouseId = (String) execution.getVariable("warehouseId");
        Integer currentStock = (Integer) execution.getVariable("currentStock");
        Integer reorderQuantity = (Integer) execution.getVariable("reorderQuantity");

        LOG.info("=== Inventory Restock Request ===");
        LOG.info("  Process Instance ID : {}", execution.getProcessInstanceId());
        LOG.info("  Product ID          : {}", productId);
        LOG.info("  Warehouse ID        : {}", warehouseId);
        LOG.info("  Current Stock       : {}", currentStock);
        LOG.info("  Reorder Quantity    : {}", reorderQuantity);
        LOG.info("  Restock request processed successfully.");
    }
}
