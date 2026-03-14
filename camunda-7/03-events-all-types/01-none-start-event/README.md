# 01. None Start Event

## 📖 What is a None Start Event?

A **None Start Event** is the most basic start event in BPMN. It has **no trigger condition** and is used to start a process instance **manually** via API, Tasklist, or programmatically.

**Key Characteristics:**
- ✅ No trigger condition (blank circle)
- ✅ Started manually or via API
- ✅ Most common start event type
- ✅ Can have multiple instances running

---

## ⚠️ No Front Task & No Back Task

A None Start Event has:

- **No front task (nothing before it)** — It is the very first element in the process. There is no trigger, no preceding task, no condition. It starts only when someone explicitly calls it.
- **No back task (nothing behind it that makes it fire)** — Unlike other start events, there is no mechanism that automatically fires this event.

That is the **whole point** of a None Start Event — a **blank circle with no trigger mechanism**.

### Comparison with Other Start Events

| Start Event Type | Has a Trigger Before It? | What Triggers It? |
|---|---|---|
| **None Start Event** ⭕ | ❌ No — manually triggered | User clicks "Start" or API call |
| **Timer Start Event** ⏰ | ✅ Yes — time/schedule | Cron job fires at midnight |
| **Message Start Event** ✉️ | ✅ Yes — message received | External system sends a message |
| **Signal Start Event** 📡 | ✅ Yes — signal broadcast | Another process broadcasts a signal |
| **Conditional Start Event** ❓ | ✅ Yes — condition met | Data condition becomes true |

> **Key Takeaway:** The None Start Event is the simplest start event. The difference between use cases is **how you call the API** (order creation, webhook, batch, test), not what happens inside the BPMN start event itself.

---

## 🎯 When to Use

| Use Case | Example | Real-World Scenario |
|----------|---------|---------------------|
| **Manual Process Start** | User initiates order processing | Customer places an order on e-commerce site |
| **API-triggered Process** | REST API starts workflow | Payment gateway sends webhook after payment |
| **On-demand Workflows** | Admin triggers batch job | Admin generates monthly invoices for all customers |
| **Testing** | Start process for testing | QA engineer runs happy-path / error-handling scenarios |

---

## 🏗️ BPMN Diagrams

### 1. Simple Process (Learning)
```
[○ Start] → [Log Message] → [● End]
```
**File:** `none-start-event-process.bpmn`

### 2. Order Processing (Real-World)
```
[○ Order Received] → [Validate Order] → [Check Inventory] → [Process Payment] → [Ship Order] → [● Order Completed]
```
**File:** `order-processing-process.bpmn`

### 3. Payment Webhook (Real-World)
```
[○ Payment Received] → [Verify Payment] → [Update Order Status] → [Send Notification] → [● Payment Processed]
```
**File:** `payment-webhook-process.bpmn`

### 4. Batch Invoice (Real-World)
```
[○ Batch Triggered] → [Fetch Customer Data] → [Generate Invoice] → [Send Invoice Email] → [● Invoice Sent]
```
**File:** `batch-invoice-process.bpmn`

**Visual Representation:**
- **○** = None Start Event (empty circle, no icon inside)
- All 4 processes use the same None Start Event — the difference is what happens **after** the start

---

## 🔧 Implementation

### BPMN Configuration

```xml
<bpmn:startEvent id="StartEvent_1" name="Start">
  <bpmn:outgoing>Flow_1</bpmn:outgoing>
</bpmn:startEvent>
```

**Properties:**
- No special attributes required
- Just a basic start event element
- No timer, no message, no signal — just a plain empty circle

---

## 📁 Project Structure

```
src/main/java/com/example/event/
├── Application.java                              # Spring Boot Main Class
│
├── controller/                                    # REST Controllers
│   ├── OrderProcessingController.java             # POST /api/orders/create
│   ├── WebhookController.java                     # POST /api/webhooks/payment-received
│   ├── BatchJobController.java                    # POST /api/admin/batch/invoice-generation
│   └── TestController.java                        # POST /api/test/scenario/{name}
│
└── delegate/                                      # Camunda JavaDelegates
    ├── LoggerDelegate.java                        # Simple logger (learning)
    │
    ├── order/                                     # Order Processing Domain
    │   ├── ValidateOrderDelegate.java
    │   ├── CheckInventoryDelegate.java
    │   ├── ProcessPaymentDelegate.java
    │   └── ShipOrderDelegate.java
    │
    ├── payment/                                   # Payment Webhook Domain
    │   ├── VerifyPaymentDelegate.java
    │   ├── UpdateOrderDelegate.java
    │   └── SendNotificationDelegate.java
    │
    └── batch/                                     # Batch Invoice Domain
        ├── FetchCustomerDataDelegate.java
        ├── GenerateInvoiceDelegate.java
        └── SendInvoiceDelegate.java

src/main/resources/
├── application.yaml
├── none-start-event-process.bpmn                  # Simple process (1 task)
├── order-processing-process.bpmn                  # Order process (4 tasks)
├── payment-webhook-process.bpmn                   # Payment process (3 tasks)
└── batch-invoice-process.bpmn                     # Batch process (3 tasks)
```

---

## 🌍 Real-World Examples

### 1. Manual Process Start — E-Commerce Order Processing

**Scenario:** Customer places an order → API triggers order fulfillment workflow.

```bash
curl -X POST http://localhost:8080/api/orders/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-2024-001",
    "customerId": "CUST-12345",
    "totalAmount": 299.99,
    "items": "Laptop, Mouse, Keyboard"
  }'
```

**Expected Console Output:**
```
✅ Validating order: ORD-2024-001 for customer: CUST-12345
📦 Checking inventory for order: ORD-2024-001 - Items: Laptop, Mouse, Keyboard
💳 Processing payment for order: ORD-2024-001 - Amount: $299.99 - Customer: CUST-12345
🚚 Shipping order: ORD-2024-001 to customer: CUST-12345
   Tracking Number: TRK-1234567890
```

---

### 2. API-Triggered Process — Payment Gateway Webhook

**Scenario:** Stripe/PayPal sends webhook after payment → triggers payment verification workflow.

```bash
curl -X POST http://localhost:8080/api/webhooks/payment-received \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-2024-5678",
    "amount": 149.99,
    "currency": "USD",
    "paymentMethod": "credit_card",
    "customerId": "CUST-12345"
  }'
```

**Expected Console Output:**
```
🔍 Verifying payment: TXN-2024-5678 - Amount: $149.99 - Method: credit_card
📝 Updating order status for transaction: TXN-2024-5678 - Customer: CUST-12345
📧 Sending payment confirmation to customer: CUST-12345
   Transaction: TXN-2024-5678 - Amount: $149.99
```

---

### 3. On-Demand Workflow — Admin Batch Invoice Generation

**Scenario:** Admin triggers monthly invoice generation for multiple customers.

```bash
curl -X POST http://localhost:8080/api/admin/batch/invoice-generation \
  -H "Content-Type: application/json" \
  -d '{
    "customerIds": ["CUST-001", "CUST-002", "CUST-003"],
    "billingPeriod": "2024-01"
  }'
```

**Expected Console Output:**
```
📊 Fetching customer data for: CUST-001
📄 Generating invoice: INV-CUST-001-2024-01
   Customer: Customer-CUST-001 - Period: 2024-01
📧 Sending invoice INV-CUST-001-2024-01 to CUST-001@example.com
   Amount: $1500.0
📊 Fetching customer data for: CUST-002
📄 Generating invoice: INV-CUST-002-2024-01
...
```

---

### 4. Testing — Automated Test Scenarios

**Scenario:** Developer/QA runs different test scenarios.

```bash
# Happy path
curl -X POST http://localhost:8080/api/test/scenario/happy-path

# Error handling
curl -X POST http://localhost:8080/api/test/scenario/error-handling

# Load test (10 instances)
curl -X POST "http://localhost:8080/api/test/load-test?instances=10"
```

---

## 🚀 Running the Example

### Step 1: Build and Start
```bash
mvn clean package
mvn spring-boot:run
```

### Step 2: Start Process via Camunda REST API (Simple Process)
```bash
curl -X POST http://localhost:8080/engine-rest/process-definition/key/none-start-event-process/start \
  -H "Content-Type: application/json" \
  -d '{}'
```

### Step 3: Start with Variables
```bash
curl -X POST http://localhost:8080/engine-rest/process-definition/key/none-start-event-process/start \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "orderId": {"value": "ORD-001", "type": "String"}
    }
  }'
```

### Step 4: Start with Business Key
```bash
curl -X POST http://localhost:8080/engine-rest/process-definition/key/none-start-event-process/start \
  -H "Content-Type: application/json" \
  -d '{"businessKey": "ORDER-123"}'
```

### Step 5: Query Running Instances
```bash
curl http://localhost:8080/engine-rest/process-instance?processDefinitionKey=none-start-event-process
```

### Step 6: Verify in Cockpit
Open: http://localhost:8080/camunda/app/cockpit  
Login: demo / demo

---

## 🎓 Key Concepts

✅ **No Front Task** — Nothing triggers it automatically; it is the first element  
✅ **No Back Task** — No mechanism behind it that makes it fire  
✅ **Manual Trigger** — Process starts only when explicitly called  
✅ **No Automatic Start** — Unlike timer, message, or signal start events  
✅ **Multiple Instances** — Can start multiple instances simultaneously  
✅ **REST API** — Primary way to start processes programmatically  
✅ **Business Keys** — Use business keys to track specific instances  
✅ **Process Variables** — Pass initial context data at start  

---

## 💡 Best Practices

1. **Use for user-initiated workflows** — Order processing, document approval
2. **Combine with business keys** — Track specific instances
3. **Pass initial variables** — Provide context at start
4. **Use in testing** — Easy to trigger for tests
5. **Keep the start event simple** — Add complexity in tasks after the start, not in the start event itself

---

## 📊 Process Summary

| Process | BPMN File | Tasks | Controller | Use Case |
|---------|-----------|-------|------------|----------|
| Simple | `none-start-event-process.bpmn` | 1 | `TestController` | Learning / Testing |
| Order | `order-processing-process.bpmn` | 4 | `OrderProcessingController` | E-Commerce |
| Payment | `payment-webhook-process.bpmn` | 3 | `WebhookController` | Payment Gateway |
| Batch | `batch-invoice-process.bpmn` | 3 | `BatchJobController` | Admin Operations |

---

## 🔗 Official Documentation

- [Start Events](https://docs.camunda.org/manual/latest/reference/bpmn20/events/start-events/)
- [Process Instance API](https://docs.camunda.org/manual/latest/reference/rest/process-definition/post-start-process-instance/)
- [BPMN 2.0 Reference](https://docs.camunda.org/manual/latest/reference/bpmn20/)

---

## 📚 Next Steps

- **[02-timer-start-event](../02-timer-start-event/)** — Scheduled process start (has a front trigger: timer/cron)
- **[03-message-start-event](../03-message-start-event/)** — Message-triggered start (has a front trigger: message)
