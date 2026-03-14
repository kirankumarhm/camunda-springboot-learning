# 03. Message Start Event

## 📖 What is a Message Start Event?

A **Message Start Event** starts a process instance when a specific message is received. It enables external systems or other processes to trigger workflow execution.

**Key Characteristics:**
- ✅ Triggered by receiving a named message
- ✅ Can pass variables with the message
- ✅ Supports message correlation
- ✅ Enables event-driven architecture

---

## 🎯 Use Cases with Real-World Examples

This project demonstrates **5 real-world use cases**, each with its own BPMN process, Java delegate, integration layer (REST API or Kafka), and message definition.

| # | Use Case | Real-World Scenario | Trigger Mechanism | Message Name | Process ID |
|---|----------|---------------------|-------------------|--------------|------------|
| 1 | **E-Commerce Order Intake** | Shopify sends a new order to the fulfillment system | REST API (Webhook) | `Msg_ECommerceOrderReceived` | `ecommerce-order-intake-process` |
| 2 | **Payment Notification Fulfillment** | Stripe sends a payment confirmation → triggers shipping | REST API (Webhook) | `Msg_PaymentConfirmed` | `payment-fulfillment-process` |
| 3 | **Inventory Restock Request** | Inventory microservice notifies procurement when stock is low | Kafka Consumer | `Msg_InventoryRestockRequested` | `inventory-restock-process` |
| 4 | **Customer Onboarding via Webhook** | KYC provider sends webhook after identity verification | REST API (Webhook) | `Msg_KycVerificationCompleted` | `customer-onboarding-process` |
| 5 | **Shipment Tracking Update** | FedEx sends shipment status update via message queue | Kafka Consumer | `Msg_ShipmentStatusUpdated` | `shipment-tracking-process` |

---

## 📖 Flow Stories (End-to-End Walkthrough)

### Use Case 1: E-Commerce Order Intake

**Real-World Story:** A customer places an order on a Shopify storefront. Shopify is configured to send a webhook to our fulfillment system whenever a new order is created. Our system receives the order, starts a Camunda process, and logs the order details for downstream processing.

```
┌─────────────┐    HTTP POST     ┌──────────────────────┐    correlate()    ┌──────────────────────┐    delegateExpression    ┌──────────────────┐
│   Shopify    │ ──────────────→ │ OrderWebhookController│ ─────────────────────────→  │ ✉️ StartEvent_        │ ──────────────────→   │ OrderIntake       │
│  Storefront  │  /api/webhook/  │                      │    Msg_ECommerceOrder       │    OrderReceived      │   ${orderIntake    │    Delegate        │
│              │  order-received │                      │    Received                 │                      │    Delegate}       │                    │
└─────────────┘                  └──────────────────────┘                              └──────────────────────┘                    └────────┬─────────┘
                                                                                                                                          │
                                                                                                                                          ▼
                                                                                                                                 ┌────────────────┐
                                                                                                                                 │ ● EndEvent_     │
                                                                                                                                 │  OrderProcessed │
                                                                                                                                 └────────────────┘
```

| Aspect | Detail |
|--------|--------|
| **Who triggers it?** | Shopify (or any e-commerce platform) via webhook |
| **Where does it enter?** | `POST /api/webhook/order-received` → `OrderWebhookController` |
| **Input (JSON payload)** | `{"orderId": "ORD-2024-00123", "customerName": "<name>", "channel": "Shopify"}` |
| **Input DTO** | `OrderReceivedRequest` (fields: orderId, customerName, channel) |
| **How is the message sent?** | Controller checks for duplicate via `ProcessInstanceQuery`, then calls `RuntimeService.createMessageCorrelation("Msg_ECommerceOrderReceived").correlate()` |
| **Which BPMN catches it?** | `ecommerce-order-intake.bpmn` → `StartEvent_OrderReceived` (listens for `Msg_ECommerceOrderReceived`) |
| **Business Key** | `orderId` (e.g., `ORD-2024-00123`) |
| **Process Variables set** | `orderId` (String), `customerName` (String), `channel` (String) |
| **Which delegate executes?** | `Task_ProcessOrder` → `OrderIntakeDelegate` (bean: `orderIntakeDelegate`) |
| **What does the delegate do?** | Reads orderId, customerName, channel from execution context and logs them via SLF4J |
| **Output (HTTP response)** | `200 OK` — `"Order process started for: ORD-2024-00123"` |
| **Output (Console log)** | Logs business key, order ID, customer name, channel, and completion message |

---

### Use Case 2: Payment Notification Fulfillment

**Real-World Story:** A customer completes payment on the checkout page. Stripe processes the charge and fires a `payment_intent.succeeded` webhook to our system. Our system receives the payment confirmation, starts a Camunda fulfillment process, and logs the transaction details so the warehouse can begin pick-pack-ship.

```
┌─────────────┐    HTTP POST       ┌────────────────────────┐    correlate()    ┌──────────────────────┐    delegateExpression    ┌──────────────────────┐
│   Stripe     │ ────────────────→ │ PaymentWebhookController│ ─────────────────────────→  │ ✉️ StartEvent_        │ ──────────────────→   │ PaymentFulfillment   │
│  Gateway     │  /api/webhook/    │                        │    Msg_PaymentConfirmed     │    PaymentConfirmed   │   ${paymentFulfill │    Delegate            │
│              │  payment-confirmed│                        │                             │                      │    mentDelegate}   │                        │
└─────────────┘                    └────────────────────────┘                              └──────────────────────┘                    └──────────┬───────────┘
                                                                                                                                                │
                                                                                                                                                ▼
                                                                                                                                     ┌─────────────────────┐
                                                                                                                                     │ ● EndEvent_          │
                                                                                                                                     │  FulfillmentInitiated│
                                                                                                                                     └─────────────────────┘
```

| Aspect | Detail |
|--------|--------|
| **Who triggers it?** | Stripe (or PayPal, Razorpay) via payment webhook |
| **Where does it enter?** | `POST /api/webhook/payment-confirmed` → `PaymentWebhookController` |
| **Input (JSON payload)** | `{"transactionId": "TXN-STRIPE-98765", "orderId": "ORD-2024-00123", "amount": 249.99, "paymentProvider": "Stripe"}` |
| **Input DTO** | `PaymentConfirmedRequest` (fields: transactionId, orderId, amount, paymentProvider) |
| **How is the message sent?** | Controller checks for duplicate via `ProcessInstanceQuery`, then calls `RuntimeService.createMessageCorrelation("Msg_PaymentConfirmed").correlate()` |
| **Which BPMN catches it?** | `payment-fulfillment.bpmn` → `StartEvent_PaymentConfirmed` (listens for `Msg_PaymentConfirmed`) |
| **Business Key** | `transactionId` (e.g., `TXN-STRIPE-98765`) |
| **Process Variables set** | `transactionId` (String), `orderId` (String), `amount` (Double), `paymentProvider` (String) |
| **Which delegate executes?** | `Task_InitiateFulfillment` → `PaymentFulfillmentDelegate` (bean: `paymentFulfillmentDelegate`) |
| **What does the delegate do?** | Reads transactionId, orderId, amount, paymentProvider from execution context and logs them via SLF4J |
| **Output (HTTP response)** | `200 OK` — `"Fulfillment process started for transaction: TXN-STRIPE-98765"` |
| **Output (Console log)** | Logs transaction ID, order ID, amount, payment provider, and fulfillment initiation message |

---

### Use Case 3: Inventory Restock Request

**Real-World Story:** A warehouse inventory microservice runs a scheduled stock check. It detects that product SKU-4567 in Warehouse WH-01 has dropped to 5 units — below the reorder threshold of 50. The microservice publishes a restock event to the `inventory.restock.requested` Kafka topic. Our Camunda consumer picks up the event and starts a procurement process to reorder 200 units from the supplier.

```
┌─────────────────┐    Kafka publish     ┌─────────────────────────┐    @KafkaListener     ┌───────────────────────────────┐    correlate()    ┌──────────────────────┐
│   Warehouse      │ ──────────────────→ │ inventory.restock.       │ ──────────────────→  │ InventoryRestockKafkaConsumer  │ ─────────────────────────→  │ ✉️ StartEvent_        │
│   Inventory      │                     │ requested (Kafka Topic)  │                      │                               │    Msg_InventoryRestock     │    RestockRequested   │
│   Microservice   │                     │                          │                      │                               │    Requested               │                      │
└─────────────────┘                      └─────────────────────────┘                       └───────────────────────────────┘                              └──────────┬───────────┘
                                                                                                                                                                    │
                                                                                                                                                                    ▼
                                                                                                                                                          ┌──────────────────────┐    ┌────────────────────────┐
                                                                                                                                                          │ ⚙️ Task_ProcessRestock│ →  │ ● EndEvent_             │
                                                                                                                                                          │  (inventoryRestock   │    │   RestockProcessed     │
                                                                                                                                                          │   Delegate)          │    └────────────────────────┘
                                                                                                                                                          └──────────────────────┘
```

| Aspect | Detail |
|--------|--------|
| **Who triggers it?** | Warehouse inventory microservice (automated stock check) |
| **Where does it enter?** | Kafka topic `inventory.restock.requested` → `InventoryRestockKafkaConsumer` |
| **Input (Kafka event)** | `{"productId": "SKU-4567", "warehouseId": "WH-01", "currentStock": 5, "reorderQuantity": 200}` |
| **Input DTO** | `InventoryRestockEvent` (fields: productId, warehouseId, currentStock, reorderQuantity) |
| **How is the message sent?** | Kafka consumer checks for duplicate via `ProcessInstanceQuery`, then calls `RuntimeService.createMessageCorrelation("Msg_InventoryRestockRequested").correlate()` |
| **Which BPMN catches it?** | `inventory-restock.bpmn` → `StartEvent_RestockRequested` (listens for `Msg_InventoryRestockRequested`) |
| **Business Key** | Generated as `"RESTOCK-" + warehouseId + "-" + productId` (e.g., `RESTOCK-WH-01-SKU-4567`) |
| **Process Variables set** | `productId` (String), `warehouseId` (String), `currentStock` (Integer), `reorderQuantity` (Integer) |
| **Which delegate executes?** | `Task_ProcessRestock` → `InventoryRestockDelegate` (bean: `inventoryRestockDelegate`) |
| **What does the delegate do?** | Reads productId, warehouseId, currentStock, reorderQuantity from execution context and logs them via SLF4J |
| **Output (Kafka consumer log)** | `"Restock process started with business key: RESTOCK-WH-01-SKU-4567"` |
| **Output (Console log)** | Logs product ID, warehouse ID, current stock, reorder quantity, and completion message |

---

### Use Case 4: Customer Onboarding via Webhook

**Real-World Story:** A fintech company requires identity verification before activating customer accounts. When a new customer signs up, they are redirected to Onfido (a third-party KYC provider) to upload their ID and take a selfie. After Onfido completes the verification, it sends a webhook callback to our system with the result (APPROVED/REJECTED). Our system receives this, starts a Camunda onboarding process, and logs the verification outcome so the account can be activated or flagged.

```
┌─────────────┐    HTTP POST     ┌──────────────────────┐    correlate()    ┌──────────────────────┐    delegateExpression    ┌──────────────────────┐
│   Onfido     │ ──────────────→ │ KycWebhookController  │ ─────────────────────────→  │ ✉️ StartEvent_        │ ──────────────────→   │ CustomerOnboarding   │
│  KYC Provider│  /api/webhook/  │                      │    Msg_KycVerification      │    KycCompleted       │   ${customerOnboard│    Delegate            │
│              │  kyc-verified   │                      │    Completed                │                      │    ingDelegate}    │                        │
└─────────────┘                  └──────────────────────┘                              └──────────────────────┘                    └──────────┬───────────┘
                                                                                                                                              │
                                                                                                                                              ▼
                                                                                                                                   ┌─────────────────────┐
                                                                                                                                   │ ● EndEvent_          │
                                                                                                                                   │  CustomerOnboarded   │
                                                                                                                                   └─────────────────────┘
```

| Aspect | Detail |
|--------|--------|
| **Who triggers it?** | Onfido (or Jumio, Veriff — any KYC provider) via webhook callback |
| **Where does it enter?** | `POST /api/webhook/kyc-verified` → `KycWebhookController` |
| **Input (JSON payload)** | `{"customerId": "CUST-2024-07890", "verificationStatus": "APPROVED", "kycProvider": "Onfido"}` |
| **Input DTO** | `KycVerificationRequest` (fields: customerId, verificationStatus, kycProvider) |
| **How is the message sent?** | Controller checks for duplicate via `ProcessInstanceQuery`, then calls `RuntimeService.createMessageCorrelation("Msg_KycVerificationCompleted").correlate()` |
| **Which BPMN catches it?** | `customer-onboarding.bpmn` → `StartEvent_KycCompleted` (listens for `Msg_KycVerificationCompleted`) |
| **Business Key** | `customerId` (e.g., `CUST-2024-07890`) |
| **Process Variables set** | `customerId` (String), `verificationStatus` (String), `kycProvider` (String) |
| **Which delegate executes?** | `Task_OnboardCustomer` → `CustomerOnboardingDelegate` (bean: `customerOnboardingDelegate`) |
| **What does the delegate do?** | Reads customerId, verificationStatus, kycProvider from execution context and logs them via SLF4J |
| **Output (HTTP response)** | `200 OK` — `"Onboarding process started for customer: CUST-2024-07890"` |
| **Output (Console log)** | Logs customer ID, verification status, KYC provider, and onboarding completion message |

---

### Use Case 5: Shipment Tracking Update

**Real-World Story:** A logistics partner (FedEx) scans a package at a distribution hub and updates its status to "OUT_FOR_DELIVERY". FedEx's tracking system publishes this status change to the `shipment.status.updated` Kafka topic. Our Camunda consumer picks up the event and starts a tracking process that can notify the customer, update the order status in the database, or trigger exception handling if the status is "LOST" or "DAMAGED".

```
┌─────────────────┐    Kafka publish     ┌─────────────────────────┐    @KafkaListener     ┌────────────────────────────────┐    correlate()    ┌──────────────────────┐
│   FedEx          │ ──────────────────→ │ shipment.status.         │ ──────────────────→  │ ShipmentTrackingKafkaConsumer   │ ─────────────────────────→  │ ✉️ StartEvent_        │
│   Tracking       │                     │ updated (Kafka Topic)    │                      │                                │    Msg_ShipmentStatus       │    ShipmentUpdated    │
│   System         │                     │                          │                      │                                │    Updated                  │                      │
└─────────────────┘                      └─────────────────────────┘                       └────────────────────────────────┘                              └──────────┬───────────┘
                                                                                                                                                                     │
                                                                                                                                                                     ▼
                                                                                                                                                          ┌───────────────────────────┐    ┌──────────────────────────┐
                                                                                                                                                          │ ⚙️ Task_ProcessShipment    │ →  │ ● EndEvent_               │
                                                                                                                                                          │    Update (shipmentTracking│    │   ShipmentProcessed       │
                                                                                                                                                          │    Delegate)               │    └──────────────────────────┘
                                                                                                                                                          └───────────────────────────┘
```

| Aspect | Detail |
|--------|--------|
| **Who triggers it?** | FedEx (or DHL, UPS — any logistics partner) tracking system |
| **Where does it enter?** | Kafka topic `shipment.status.updated` → `ShipmentTrackingKafkaConsumer` |
| **Input (Kafka event)** | `{"trackingNumber": "FEDEX-1234567890", "carrier": "FedEx", "status": "OUT_FOR_DELIVERY", "orderId": "ORD-2024-00123"}` |
| **Input DTO** | `ShipmentStatusEvent` (fields: trackingNumber, carrier, status, orderId) |
| **How is the message sent?** | Kafka consumer checks for duplicate via `ProcessInstanceQuery`, then calls `RuntimeService.createMessageCorrelation("Msg_ShipmentStatusUpdated").correlate()` |
| **Which BPMN catches it?** | `shipment-tracking.bpmn` → `StartEvent_ShipmentUpdated` (listens for `Msg_ShipmentStatusUpdated`) |
| **Business Key** | Generated as `"TRACK-" + carrier.toUpperCase() + "-" + trackingNumber` (e.g., `TRACK-FEDEX-FEDEX-1234567890`) |
| **Process Variables set** | `trackingNumber` (String), `carrier` (String), `status` (String), `orderId` (String) |
| **Which delegate executes?** | `Task_ProcessShipmentUpdate` → `ShipmentTrackingDelegate` (bean: `shipmentTrackingDelegate`) |
| **What does the delegate do?** | Reads trackingNumber, carrier, status, orderId from execution context and logs them via SLF4J |
| **Output (Kafka consumer log)** | `"Shipment tracking process started with business key: TRACK-FEDEX-FEDEX-1234567890"` |
| **Output (Console log)** | Logs tracking number, carrier, status, order ID, and completion message |

---

## 🏗️ BPMN Diagrams

Each use case follows the same Message Start Event pattern:

```
[✉️ Message Start Event] → [⚙️ Service Task] → [● End Event]
```

### Why No Task Before the Message Start Event?

By **BPMN specification**, a Start Event is always the **first element** in a process. It cannot have any incoming sequence flows — nothing can precede it within the process boundary.

The trigger comes from **outside** the process:

```
┌─────────────────────────────────────────────────────┐
│  External System (Shopify, Stripe, FedEx, etc.)     │
│  Sends message via REST API / Kafka / Webhook       │
└──────────────────────┬──────────────────────────────┘
                       │ message
                       ▼
┌──────────────────────────────────────────────────────┐
│  BPMN Process Boundary                               │
│                                                      │
│  [✉️ Message Start Event] → [⚙️ Task] → [● End]     │
│                                                      │
└──────────────────────────────────────────────────────┘
```

| Position | Can we add a task? | Reason |
|---|---|---|
| **Before** Message Start Event | ❌ No | BPMN spec — Start Events have no incoming sequence flows |
| **After** Service Task | ✅ Yes | You can chain as many tasks as needed after the start event |

### Why Only One Service Task After the Start Event?

The flow after the Message Start Event is intentionally kept minimal (one Service Task → End Event) because:

1. **Learning focus** — This module isolates the **Message Start Event** concept. Adding more tasks would shift focus to Service Task chaining, which is a different topic.
2. **Proof of concept** — A single delegate is sufficient to prove that the message triggered the process and all variables were passed correctly.
3. **Extensibility** — In a real-world scenario, you would add more tasks (gateways, user tasks, error handling) after the start event. For example:

```
Real-world extended flow:
[✉️ Message Start] → [⚙️ Validate Order] → [◇ Gateway] → [⚙️ Process Payment] → [⚙️ Send Confirmation] → [● End]

This module's learning flow:
[✉️ Message Start] → [⚙️ Process Order] → [● End]
```

### Artifact Mapping

| Use Case | BPMN File | Java Delegate | Integration Layer |
|----------|-----------|---------------|-------------------|
| E-Commerce Order Intake | `ecommerce-order-intake.bpmn` | `OrderIntakeDelegate` | `OrderWebhookController` |
| Payment Notification Fulfillment | `payment-fulfillment.bpmn` | `PaymentFulfillmentDelegate` | `PaymentWebhookController` |
| Inventory Restock Request | `inventory-restock.bpmn` | `InventoryRestockDelegate` | `InventoryRestockKafkaConsumer` |
| Customer Onboarding via Webhook | `customer-onboarding.bpmn` | `CustomerOnboardingDelegate` | `KycWebhookController` |
| Shipment Tracking Update | `shipment-tracking.bpmn` | `ShipmentTrackingDelegate` | `ShipmentTrackingKafkaConsumer` |

---

## 🔌 Integration Architecture

### How External Systems Trigger Camunda Processes

This project demonstrates **two real-world integration patterns** for triggering Message Start Events:

```
┌──────────────────────────────────────────────────────────────────────────┐
│  Pattern 1: REST API (Webhook)                                          │
│                                                                          │
│  [Shopify/Stripe/Onfido] → HTTP POST → [Spring @RestController]         │
│                                          → RuntimeService                │
│                                            .correlate()                   │
│                                          → [✉️ Message Start Event]      │
└──────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│  Pattern 2: Kafka Consumer                                               │
│                                                                          │
│  [Warehouse/FedEx] → Kafka Topic → [Spring @KafkaListener]              │
│                                      → RuntimeService                    │
│                                        .correlate()                      │
│                                      → [✉️ Message Start Event]          │
└──────────────────────────────────────────────────────────────────────────┘
```

### REST API Controllers (Use Cases 1, 2, 4)

External systems send HTTP POST requests to custom webhook endpoints. The controller receives the payload, checks for duplicate running instances, maps it to process variables, and uses `RuntimeService.correlate()` to trigger the BPMN process.

| Endpoint | Use Case | Controller |
|----------|----------|------------|
| `POST /api/webhook/order-received` | E-Commerce Order Intake | `OrderWebhookController` |
| `POST /api/webhook/payment-confirmed` | Payment Notification Fulfillment | `PaymentWebhookController` |
| `POST /api/webhook/kyc-verified` | Customer Onboarding via Webhook | `KycWebhookController` |

### Kafka Consumers (Use Cases 3, 5)

Microservices publish events to Kafka topics. Spring Kafka listeners consume the events, check for duplicate running instances, and use `RuntimeService.correlate()` to trigger the BPMN process.

| Kafka Topic | Use Case | Consumer |
|-------------|----------|----------|
| `inventory.restock.requested` | Inventory Restock Request | `InventoryRestockKafkaConsumer` |
| `shipment.status.updated` | Shipment Tracking Update | `ShipmentTrackingKafkaConsumer` |

### Why Not Just Use `/engine-rest/message`?

Camunda's built-in REST endpoint works, but real-world systems need:
- **Payload transformation** — External systems send domain-specific JSON, not Camunda's message format
- **Validation** — Verify required fields before starting a process
- **Authentication** — Webhook signature verification (e.g., Stripe's `Stripe-Signature` header)
- **Decoupling** — Kafka consumers decouple producers from Camunda's API format
- **Error handling** — Custom error responses and retry logic

The built-in `/engine-rest/message` endpoint is still available for direct testing (see Testing section below).

---

## 📁 Project Structure

```
03-message-start-event/
├── pom.xml
├── docker-compose.yml                          # Kafka + Zookeeper
├── README.md
└── src/main/
    ├── java/com/example/event/
    │   ├── Application.java
    │   ├── controller/                          # REST API (Webhook receivers)
    │   │   ├── OrderWebhookController.java      # UC1: Shopify order webhook
    │   │   ├── PaymentWebhookController.java    # UC2: Stripe payment webhook
    │   │   └── KycWebhookController.java        # UC4: KYC provider webhook
    │   ├── kafka/                               # Kafka Consumers
    │   │   ├── InventoryRestockKafkaConsumer.java  # UC3: Inventory restock event
    │   │   └── ShipmentTrackingKafkaConsumer.java  # UC5: Shipment status event
    │   ├── delegate/                            # Camunda Java Delegates
    │   │   ├── OrderIntakeDelegate.java
    │   │   ├── PaymentFulfillmentDelegate.java
    │   │   ├── InventoryRestockDelegate.java
    │   │   ├── CustomerOnboardingDelegate.java
    │   │   └── ShipmentTrackingDelegate.java
    │   └── dto/                                 # Data Transfer Objects
    │       ├── OrderReceivedRequest.java
    │       ├── PaymentConfirmedRequest.java
    │       ├── KycVerificationRequest.java
    │       ├── InventoryRestockEvent.java
    │       └── ShipmentStatusEvent.java
    └── resources/
        ├── application.yaml
        ├── ecommerce-order-intake.bpmn
        ├── payment-fulfillment.bpmn
        ├── inventory-restock.bpmn
        ├── customer-onboarding.bpmn
        └── shipment-tracking.bpmn
```

---

## 🔧 Naming Convention

All artifacts follow a consistent naming standard:

| Artifact | Convention | Example |
|----------|-----------|---------|
| **Message Name** | `Msg_<DomainAction>` (PascalCase) | `Msg_ECommerceOrderReceived` |
| **Process ID** | `<domain>-<action>-process` (kebab-case) | `ecommerce-order-intake-process` |
| **BPMN File** | `<domain>-<action>.bpmn` (kebab-case) | `ecommerce-order-intake.bpmn` |
| **Start Event ID** | `StartEvent_<Action>` (PascalCase) | `StartEvent_OrderReceived` |
| **Service Task ID** | `Task_<Action>` (PascalCase) | `Task_ProcessOrder` |
| **End Event ID** | `EndEvent_<Result>` (PascalCase) | `EndEvent_OrderProcessed` |
| **Delegate Bean** | `<domainAction>Delegate` (camelCase) | `orderIntakeDelegate` |
| **Delegate Class** | `<DomainAction>Delegate` (PascalCase) | `OrderIntakeDelegate` |
| **Controller Class** | `<Domain>WebhookController` (PascalCase) | `OrderWebhookController` |
| **Kafka Consumer Class** | `<Domain>KafkaConsumer` (PascalCase) | `InventoryRestockKafkaConsumer` |
| **DTO Class** | `<DomainAction>Request` / `<DomainAction>Event` | `OrderReceivedRequest` / `ShipmentStatusEvent` |
| **Kafka Topic** | `<domain>.<action>.<past-tense>` (dot-separated) | `inventory.restock.requested` |
| **REST Endpoint** | `/api/webhook/<action>` (kebab-case) | `/api/webhook/order-received` |
| **Sequence Flow ID** | `Flow_<number>` | `Flow_1`, `Flow_2` |

---

## 🚀 Running the Example

### Step 1: Start Kafka (required for Use Cases 3 and 5)
```bash
docker-compose up -d
```

> **Note:** Use Cases 1, 2, and 4 (REST API) work without Kafka. If you skip this step, the app still starts but Kafka consumers will log connection warnings until the broker is available.

### Step 2: Build and Start
```bash
mvn clean package
mvn spring-boot:run
```

### Step 3: Verify in Cockpit
Open: http://localhost:8080/camunda/app/cockpit  
Login: demo / demo  
You should see all 5 process definitions deployed.

---

## 📊 Testing Each Use Case

Each use case can be tested in **two ways**:
1. **Via Custom REST API / Kafka** — The real-world integration pattern
2. **Via Camunda REST API** — Direct message correlation (for quick testing)

---

### Use Case 1: E-Commerce Order Intake (REST API)

**Scenario:** An e-commerce platform (e.g., Shopify, Amazon Marketplace) receives a new customer order and sends it to the fulfillment system for processing.

**Option A: Via Custom Webhook Endpoint**
```bash
curl -X POST http://localhost:8080/api/webhook/order-received \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-2024-00123",
    "customerName": "<name>",
    "channel": "Shopify"
  }'
```

**Option B: Via Camunda REST API (Direct)**
```bash
curl -X POST http://localhost:8080/engine-rest/message \
  -H "Content-Type: application/json" \
  -d '{
    "messageName": "Msg_ECommerceOrderReceived",
    "businessKey": "ORD-2024-00123",
    "processVariables": {
      "orderId": {"value": "ORD-2024-00123", "type": "String"},
      "customerName": {"value": "<name>", "type": "String"},
      "channel": {"value": "Shopify", "type": "String"}
    }
  }'
```

**Expected Log Output:**
```
Webhook received: E-Commerce Order [ORD-2024-00123] from channel [Shopify]
=== E-Commerce Order Intake ===
  Process Instance ID : <generated-id>
  Business Key        : ORD-2024-00123
  Order ID            : ORD-2024-00123
  Customer            : <name>
  Channel             : Shopify
  Order intake completed successfully.
```

---

### Use Case 2: Payment Notification Fulfillment (REST API)

**Scenario:** A payment gateway (e.g., Stripe) sends a `payment_intent.succeeded` webhook. The system receives this confirmation and triggers the order fulfillment (pick, pack, ship).

**Option A: Via Custom Webhook Endpoint**
```bash
curl -X POST http://localhost:8080/api/webhook/payment-confirmed \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-STRIPE-98765",
    "orderId": "ORD-2024-00123",
    "amount": 249.99,
    "paymentProvider": "Stripe"
  }'
```

**Option B: Via Camunda REST API (Direct)**
```bash
curl -X POST http://localhost:8080/engine-rest/message \
  -H "Content-Type: application/json" \
  -d '{
    "messageName": "Msg_PaymentConfirmed",
    "businessKey": "TXN-STRIPE-98765",
    "processVariables": {
      "transactionId": {"value": "TXN-STRIPE-98765", "type": "String"},
      "orderId": {"value": "ORD-2024-00123", "type": "String"},
      "amount": {"value": 249.99, "type": "Double"},
      "paymentProvider": {"value": "Stripe", "type": "String"}
    }
  }'
```

**Expected Log Output:**
```
Webhook received: Payment [TXN-STRIPE-98765] confirmed by [Stripe]
=== Payment Notification Fulfillment ===
  Process Instance ID : <generated-id>
  Transaction ID      : TXN-STRIPE-98765
  Order ID            : ORD-2024-00123
  Amount              : 249.99
  Payment Provider    : Stripe
  Fulfillment initiated successfully.
```

---

### Use Case 3: Inventory Restock Request (Kafka)

**Scenario:** A warehouse inventory microservice detects that stock for a product has fallen below the reorder threshold. It publishes an event to the `inventory.restock.requested` Kafka topic. The Camunda consumer reads the event and starts the procurement process.

**Prerequisites:** Kafka must be running (`docker-compose up -d`).

**Option A: Via Kafka (publish to topic)**
```bash
echo 'SKU-4567:{"productId":"SKU-4567","warehouseId":"WH-01","currentStock":5,"reorderQuantity":200}' | \
docker exec -i kafka kafka-console-producer \
  --broker-list localhost:9092 \
  --topic inventory.restock.requested \
  --property "parse.key=true" \
  --property "key.separator=:"
```

**Option B: Via Camunda REST API (Direct)**
```bash
curl -X POST http://localhost:8080/engine-rest/message \
  -H "Content-Type: application/json" \
  -d '{
    "messageName": "Msg_InventoryRestockRequested",
    "businessKey": "RESTOCK-WH01-SKU4567",
    "processVariables": {
      "productId": {"value": "SKU-4567", "type": "String"},
      "warehouseId": {"value": "WH-01", "type": "String"},
      "currentStock": {"value": 5, "type": "Integer"},
      "reorderQuantity": {"value": 200, "type": "Integer"}
    }
  }'
```

**Expected Log Output:**
```
Kafka event received: Restock requested for product [SKU-4567] in warehouse [WH-01]
=== Inventory Restock Request ===
  Process Instance ID : <generated-id>
  Product ID          : SKU-4567
  Warehouse ID        : WH-01
  Current Stock       : 5
  Reorder Quantity    : 200
  Restock request processed successfully.
Restock process started with business key: RESTOCK-WH-01-SKU-4567
```

---

### Use Case 4: Customer Onboarding via Webhook (REST API)

**Scenario:** A fintech company uses a third-party KYC provider (e.g., Jumio, Onfido). After the customer completes identity verification, the KYC provider sends a webhook callback. This triggers the customer onboarding process to activate the account.

**Option A: Via Custom Webhook Endpoint**
```bash
curl -X POST http://localhost:8080/api/webhook/kyc-verified \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-2024-07890",
    "verificationStatus": "APPROVED",
    "kycProvider": "Onfido"
  }'
```

**Option B: Via Camunda REST API (Direct)**
```bash
curl -X POST http://localhost:8080/engine-rest/message \
  -H "Content-Type: application/json" \
  -d '{
    "messageName": "Msg_KycVerificationCompleted",
    "businessKey": "CUST-2024-07890",
    "processVariables": {
      "customerId": {"value": "CUST-2024-07890", "type": "String"},
      "verificationStatus": {"value": "APPROVED", "type": "String"},
      "kycProvider": {"value": "Onfido", "type": "String"}
    }
  }'
```

**Expected Log Output:**
```
Webhook received: KYC verification [APPROVED] for customer [CUST-2024-07890]
=== Customer Onboarding via Webhook ===
  Process Instance ID   : <generated-id>
  Customer ID           : CUST-2024-07890
  Verification Status   : APPROVED
  KYC Provider          : Onfido
  Customer onboarding completed successfully.
```

---

### Use Case 5: Shipment Tracking Update (Kafka)

**Scenario:** A logistics partner (e.g., FedEx, DHL) publishes shipment status updates to the `shipment.status.updated` Kafka topic. The Camunda consumer reads the event and triggers downstream notifications or exception handling.

**Prerequisites:** Kafka must be running (`docker-compose up -d`).

**Option A: Via Kafka (publish to topic)**
```bash
echo 'FEDEX-1234567890:{"trackingNumber":"FEDEX-1234567890","carrier":"FedEx","status":"OUT_FOR_DELIVERY","orderId":"ORD-2024-00123"}' | \
docker exec -i kafka kafka-console-producer \
  --broker-list localhost:9092 \
  --topic shipment.status.updated \
  --property "parse.key=true" \
  --property "key.separator=:"
```

**Option B: Via Camunda REST API (Direct)**
```bash
curl -X POST http://localhost:8080/engine-rest/message \
  -H "Content-Type: application/json" \
  -d '{
    "messageName": "Msg_ShipmentStatusUpdated",
    "businessKey": "TRACK-FEDEX-1234567890",
    "processVariables": {
      "trackingNumber": {"value": "FEDEX-1234567890", "type": "String"},
      "carrier": {"value": "FedEx", "type": "String"},
      "status": {"value": "OUT_FOR_DELIVERY", "type": "String"},
      "orderId": {"value": "ORD-2024-00123", "type": "String"}
    }
  }'
```

**Expected Log Output:**
```
Kafka event received: Shipment [FEDEX-1234567890] status [OUT_FOR_DELIVERY] from carrier [FedEx]
=== Shipment Tracking Update ===
  Process Instance ID : <generated-id>
  Tracking Number     : FEDEX-1234567890
  Carrier             : FedEx
  Status              : OUT_FOR_DELIVERY
  Order ID            : ORD-2024-00123
  Shipment update processed successfully.
Shipment tracking process started with business key: TRACK-FEDEX-FEDEX-1234567890
```

---

## 🔍 Message Correlation

### `correlate()` vs `correlateStartMessage()`

This project uses `correlate()` instead of `correlateStartMessage()`. Here's why:

| Method | Behavior | Use When |
|--------|----------|----------|
| `correlateStartMessage()` | **Always** creates a new process instance | You only need to start new instances |
| `correlate()` | Starts a new instance **OR** correlates to a running instance (if one has a matching intermediate catch event) | You want flexibility for future BPMN changes |

Since our current BPMNs have no intermediate catch events, `correlate()` behaves identically to `correlateStartMessage()` — it starts a new instance. But if you later add a Message Intermediate Catch Event to the BPMN, `correlate()` will automatically route to the running instance instead of creating a duplicate.

### How Message Correlation Works (What Is the Correlation ID?)

A common question: **"What is the correlation ID in this project?"** The answer: there is no separate `correlationId` field. In Camunda 7, the **message name** and the **business key** together form the correlation criteria.

When you call:

```java
runtimeService.createMessageCorrelation(MESSAGE_NAME)
        .processInstanceBusinessKey(businessKey)
        .setVariable("orderId", event.getOrderId())
        .correlate();
```

Camunda resolves the target using **two levels of matching**:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Level 1: MESSAGE NAME (Which process definition?)                          │
│                                                                             │
│  "Msg_ECommerceOrderReceived"  →  ecommerce-order-intake.bpmn              │
│  "Msg_PaymentConfirmed"        →  payment-fulfillment.bpmn                 │
│  "Msg_InventoryRestockRequested" → inventory-restock.bpmn                  │
│  "Msg_KycVerificationCompleted"  → customer-onboarding.bpmn               │
│  "Msg_ShipmentStatusUpdated"     → shipment-tracking.bpmn                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Level 2: BUSINESS KEY (Which specific instance?)                           │
│                                                                             │
│  For Start Events  → Sets the business key on the new instance             │
│  For Catch Events  → Finds the running instance with this business key     │
└─────────────────────────────────────────────────────────────────────────────┘
```

| Criterion | Purpose | Example |
|-----------|---------|--------|
| Message Name | Identifies **which BPMN process** should handle the message | `Msg_ECommerceOrderReceived` → `ecommerce-order-intake.bpmn` |
| Business Key | Identifies **which specific instance** (for duplicate check + future intermediate catch events) | `ORD-2024-00123` |

#### Business Key as the Correlation Identifier (Per Use Case)

| Use Case | Business Key | How It's Built |
|----------|-------------|----------------|
| E-Commerce Order | `ORD-2024-00123` | Directly from `orderId` |
| Payment Fulfillment | `TXN-STRIPE-98765` | Directly from `transactionId` |
| Inventory Restock | `RESTOCK-WH-01-SKU-4567` | `"RESTOCK-" + warehouseId + "-" + productId` |
| Customer Onboarding | `CUST-2024-07890` | Directly from `customerId` |
| Shipment Tracking | `TRACK-FEDEX-FEDEX-1234567890` | `"TRACK-" + carrier.toUpperCase() + "-" + trackingNumber` |

#### Why Business Key and Not a Separate Correlation ID?

In Camunda 7, the business key **is** the built-in correlation mechanism. A separate `correlationId` field is unnecessary because:

1. **For Start Events** — The message name alone is enough to start a new instance. The business key is set on the instance for identification and duplicate prevention.
2. **For Intermediate Catch Events** (not in this project, but relevant) — Camunda uses `processInstanceBusinessKey(businessKey)` to find the **running** instance that's waiting for that message.
3. **For duplicate prevention** — We query by business key before correlating to avoid starting the same process twice.

#### When Would You Need Variable-Based Correlation?

If you had a process with a **Message Intermediate Catch Event** and multiple running instances sharing the same business key, you'd use `processVariablesEqual()` for finer-grained correlation:

```java
// Variable-based correlation (for advanced scenarios)
runtimeService.createMessageCorrelation("Msg_PaymentReceived")
        .processInstanceVariableEquals("orderId", "ORD-2024-00123")
        .correlate();
```

This matches the message to the specific running instance where the process variable `orderId` equals `ORD-2024-00123`. This is useful when:
- Multiple instances have the same business key
- You need to correlate on a domain-specific field that isn't the business key
- The process has multiple intermediate catch events for different correlation keys

#### Correlation Summary

```
┌──────────────────────────────────────────────────────────────────────────┐
│  This Project (Message Start Events)                                     │
│                                                                          │
│  Correlation = Message Name + Business Key                               │
│  • Message Name  → picks the BPMN process definition                    │
│  • Business Key  → identifies the instance + prevents duplicates         │
└──────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│  Advanced Scenarios (Intermediate Catch Events)                           │
│                                                                          │
│  Correlation = Message Name + Business Key OR Process Variables           │
│  • Message Name  → picks the BPMN process definition                    │
│  • Business Key  → finds the waiting instance                           │
│  • Variables     → finer-grained match when business key isn't enough   │
└──────────────────────────────────────────────────────────────────────────┘
```

### Duplicate Prevention via Business Key

All controllers and Kafka consumers check for a running process with the same business key **before** correlating:

```java
ProcessInstance existing = runtimeService.createProcessInstanceQuery()
        .processDefinitionKey(PROCESS_KEY)
        .processInstanceBusinessKey(businessKey)
        .singleResult();

if (existing != null) {
    LOG.warn("Process already running for business key: {}. Skipping.", businessKey);
    return;
}

runtimeService.createMessageCorrelation(MESSAGE_NAME)
        .processInstanceBusinessKey(businessKey)
        .setVariable(...)
        .correlate();
```

This prevents scenarios like:
- Shopify sending the same order webhook twice
- Kafka replaying the same restock event after a consumer restart
- Stripe retrying a payment webhook due to a timeout

| Use Case | Business Key (Correlation Key) | Prevents Duplicate |
|----------|-------------------------------|--------------------|
| E-Commerce Order | `orderId` (e.g., `ORD-2024-00123`) | Same order processed twice |
| Payment Fulfillment | `transactionId` (e.g., `TXN-STRIPE-98765`) | Same payment fulfilled twice |
| Inventory Restock | `RESTOCK-{warehouseId}-{productId}` | Same product restocked twice in same warehouse |
| Customer Onboarding | `customerId` (e.g., `CUST-2024-07890`) | Same customer onboarded twice |
| Shipment Tracking | `TRACK-{CARRIER}-{trackingNumber}` | Same shipment tracked twice |

### Without Business Key (Creates New Instance)
```bash
curl -X POST http://localhost:8080/engine-rest/message \
  -H "Content-Type: application/json" \
  -d '{"messageName": "Msg_ECommerceOrderReceived"}'
```
**Result:** A new process instance is created each time (no duplicate check via Camunda REST API).

### With Business Key
```bash
curl -X POST http://localhost:8080/engine-rest/message \
  -H "Content-Type: application/json" \
  -d '{
    "messageName": "Msg_ECommerceOrderReceived",
    "businessKey": "ORD-2024-00456",
    "processVariables": {
      "orderId": {"value": "ORD-2024-00456", "type": "String"}
    }
  }'
```
**Result:** Process instance created with business key `ORD-2024-00456` for tracking.

> **Note:** The built-in `/engine-rest/message` endpoint does **not** perform duplicate checking. Only the custom controllers and Kafka consumers have this protection.

---

## 🧪 Verification Commands

### Query All Process Instances (per use case)
```bash
# E-Commerce Order Intake
curl "http://localhost:8080/engine-rest/process-instance?processDefinitionKey=ecommerce-order-intake-process"

# Payment Fulfillment
curl "http://localhost:8080/engine-rest/process-instance?processDefinitionKey=payment-fulfillment-process"

# Inventory Restock
curl "http://localhost:8080/engine-rest/process-instance?processDefinitionKey=inventory-restock-process"

# Customer Onboarding
curl "http://localhost:8080/engine-rest/process-instance?processDefinitionKey=customer-onboarding-process"

# Shipment Tracking
curl "http://localhost:8080/engine-rest/process-instance?processDefinitionKey=shipment-tracking-process"
```

### Query by Business Key
```bash
curl "http://localhost:8080/engine-rest/process-instance?businessKey=ORD-2024-00123"
```

### View Process History
```bash
curl "http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=ecommerce-order-intake-process"
```

---

## 📋 Message vs None Start Event

| Feature | None Start Event | Message Start Event |
|---------|------------------|---------------------|
| **Trigger** | Manual / REST API | Named message received |
| **External Integration** | Limited | Excellent |
| **Event-Driven** | No | Yes |
| **Variables** | Via start API | Via message payload |
| **Use Case** | User-initiated | System-initiated |

---

## 🎓 Key Concepts Learned

✅ **Message Start Event** – Event-driven process instantiation  
✅ **Message Correlation** – Linking messages to process definitions via `RuntimeService.correlate()`  
✅ **Correlation Criteria** – Message name identifies the process definition; business key identifies the instance  
✅ **Business Key as Correlation ID** – No separate `correlationId` needed; business key is Camunda 7's built-in correlation mechanism  
✅ **Variable-Based Correlation** – Using `processVariablesEqual()` for advanced scenarios with intermediate catch events  
✅ **Duplicate Prevention** – Querying running instances by business key before starting new ones  
✅ **Process Variables** – Passing data via message payloads  
✅ **Business Keys** – Identifying and tracking process instances  
✅ **REST API Integration** – Custom `@RestController` webhook endpoints that correlate messages to Camunda  
✅ **Kafka Integration** – `@KafkaListener` consumers that correlate Kafka events to Camunda processes  
✅ **One Process Per BPMN** – Camunda best practice for maintainability  
✅ **Naming Conventions** – Consistent `Msg_`, `Task_`, `StartEvent_` prefixes  

---

## 💡 Best Practices

1. **Use descriptive message names** – `Msg_ECommerceOrderReceived` not `msg1`
2. **One process per BPMN file** – Easier to maintain and version
3. **Use business keys** – For tracking and correlation
4. **Use `correlate()` with duplicate check** – More flexible than `correlateStartMessage()`, supports future BPMN changes
5. **Prevent duplicates via business key query** – Always check for running instances before starting a new one
6. **Use DTOs for payloads** – Type-safe request/event objects instead of raw maps
7. **Handle message failures** – Implement error handling in delegates and controllers
8. **Document message contracts** – Define expected variables per message
9. **Use SLF4J logging** – Not `System.out.println` in production code
10. **Set `historyTimeToLive`** – Required by Camunda to avoid warnings
11. **Set `missing-topics-fatal: false`** – Prevents app crash when Kafka topics don't exist yet
12. **Extract constants** – Use `PROCESS_KEY` and `MESSAGE_NAME` constants instead of inline strings

---

## 🐳 Infrastructure

### Docker Compose (Kafka + Zookeeper)

```bash
# Start Kafka
docker-compose up -d

# Verify Kafka is running
docker-compose ps

# View Kafka logs
docker-compose logs -f kafka

# Stop Kafka
docker-compose down
```

### Kafka Topics (auto-created on first message)

| Topic | Use Case | Producer |
|-------|----------|----------|
| `inventory.restock.requested` | Inventory Restock Request | Warehouse inventory microservice |
| `shipment.status.updated` | Shipment Tracking Update | Logistics partner (FedEx, DHL) |

---

## 🐛 Troubleshooting

### Issue: Message not starting process
**Error:** `No process definition found`  
**Solution:** Ensure the message name matches exactly (e.g., `Msg_ECommerceOrderReceived`)

### Issue: Variables not available in delegate
**Solution:** Check variable names and types in the message payload match what the delegate expects

### Issue: Multiple instances created unexpectedly
**Explanation:** Each message to a Message Start Event creates a new instance — this is expected BPMN behavior

### Issue: `historyTimeToLive` warning
**Solution:** Set `camunda:historyTimeToLive="P5D"` on each `<bpmn:process>` element

### Issue: Kafka connection refused
**Error:** `Connection to node -1 (localhost/127.0.0.1:9092) could not be established`  
**Solution:** Start Kafka with `docker-compose up -d`. REST API use cases (1, 2, 4) work without Kafka.

### Issue: Kafka deserialization error
**Solution:** Ensure `spring.json.trusted.packages` in `application.yaml` includes `com.example.event.dto`

---

## 🔗 Official Documentation

- [Message Events](https://docs.camunda.org/manual/latest/reference/bpmn20/events/message-events/)
- [Message Correlation](https://docs.camunda.org/manual/latest/reference/bpmn20/events/message-events/#message-api)
- [REST API - Message](https://docs.camunda.org/manual/latest/reference/rest/message/post-message/)
- [Spring Kafka Documentation](https://docs.spring.io/spring-kafka/reference/)

---

## 📚 Next Steps

- **[04-signal-start-event](../04-signal-start-event/)** – Broadcast-triggered start
- **[06-message-intermediate-catch](../06-message-intermediate-catch/)** – Wait for message during process
- **[11-message-intermediate-throw](../11-message-intermediate-throw/)** – Send message during process
