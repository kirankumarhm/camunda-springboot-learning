# 05. Conditional Start Event

## 📖 What is a Conditional Start Event?

A **Conditional Start Event** starts a process instance when a specified condition evaluates to `true`. It uses UEL (Unified Expression Language) to evaluate process variables and triggers the process automatically when the condition is met.

**Key Characteristics:**
- Condition-based triggering using `${expression}` syntax
- Evaluates variables passed via the Condition Evaluation API
- In Camunda 7, requires explicit trigger via REST or Java API (not automatic monitoring)

---

## 🏭 Real-World Scenario: Data Center Environmental Monitoring

This example models a **real-world data center temperature monitoring system** with actual database persistence, a proper REST API, and Camunda process orchestration.

**The business problem:** Data centers must maintain server room temperatures within ASHRAE TC 9.9 recommended ranges (18–27°C). When IoT sensors detect temperatures above the threshold, the response must be automated and severity-dependent — a 32°C reading needs an email to the ops team, but a 45°C reading needs emergency cooling activation and an on-call engineer page within seconds.

**What makes this real-world (not just a simulation):**
- A proper REST API (`POST /api/sensor/readings`) acts as the IoT sensor gateway — callers don't need to know about Camunda
- Alert records are persisted to a database via JPA (H2 in dev, swap to PostgreSQL in production)
- A query endpoint (`GET /api/sensor/alerts`) returns alert history from the database
- H2 Console available at `/h2-console` to inspect persisted data directly

**How it works end-to-end:**

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│  EXTERNAL: IoT sensor gateway publishes temperature readings via MQTT                   │
│  A microservice consumes the reading and calls POST /api/sensor/readings                │
└──────────────────────────────────────┬──────────────────────────────────────────────────┘
                                       ↓
┌──────────────────────────────────────────────────────────────────────────────────────────┐
│  REST CONTROLLER: SensorGatewayController                                                │
│  Converts JSON → Camunda variables, calls runtimeService.createConditionEvaluation()     │
└──────────────────────────────────────┬──────────────────────────────────────────────────┘
                                       ↓
┌──────────────────────────────────────────────────────────────────────────────────────────┐
│  BPMN PROCESS: datacenter-temperature-alert                                              │
│                                                                                          │
│  [📋 Temp > 27°C]                                                                       │
│       ↓                                                                                  │
│  [⚙ Validate Sensor Reading]  ← Front task: check plausibility, enrich from CMDB        │
│       ↓                                                                                  │
│  <X Severity?>                                                                           │
│    ├─ WARNING (28–40°C) → [⚙ Send Warning Email]  ─────────────────────→ <X Merge>      │
│    └─ DANGER  (>40°C)   → [⚙ Activate Emergency Cooling] → [⚙ Page On-Call] → <X Merge>│
│                                                                              ↓           │
│  [⚙ Record Audit Trail]  ← Back task: persists AlertRecord to database via JPA          │
│       ↓                                                                                  │
│  [● Alert Resolved]                                                                      │
└──────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 What Comes Before and After the Conditional Start?

**Nothing comes before it inside the BPMN** — a Conditional Start Event is the entry point. But in the real architecture:

| Layer | What | How |
|-------|------|-----|
| **Before (external)** | IoT sensor gateway microservice | Consumes MQTT readings, calls `POST /api/sensor/readings` |
| **REST Controller** | `SensorGatewayController` | Converts JSON to Camunda variables, triggers condition evaluation via `runtimeService` |
| **Front task** | `Validate Sensor Reading` | First task after start — validates plausibility (-40 to 150°C range), enriches from sensor registry (CMDB lookup for location, rack ID), classifies severity using ASHRAE thresholds |
| **Core tasks** | Severity-dependent routing | WARNING path: email ops team. DANGER path: activate BMS emergency cooling + page on-call engineer via PagerDuty |
| **Back task** | `Record Audit Trail` | Last task before end — persists an `AlertRecord` entity to the database via JPA |
| **After (query)** | `GET /api/sensor/alerts` | Returns persisted alert history, filterable by severity |

---

## 🏗️ Project Structure

```
src/main/java/com/example/event/
├── Application.java                                    # Spring Boot entry point
├── controller/
│   └── SensorGatewayController.java                    # REST API: IoT sensor gateway
├── model/
│   ├── SensorReading.java                              # Domain object: sensor data (Serializable)
│   ├── AlertSeverity.java                              # Enum: NORMAL / WARNING / DANGER
│   └── AlertRecord.java                                # JPA entity: persisted audit record
├── repository/
│   └── AlertRecordRepository.java                      # Spring Data JPA repository
├── service/
│   ├── SensorRegistryService.java                      # CMDB lookup: sensor → location, rack
│   ├── CoolingSystemService.java                       # BMS integration: cooling controls
│   ├── IncidentManagementService.java                  # PagerDuty integration: on-call paging
│   └── AuditService.java                               # Persists AlertRecord via JPA
└── delegate/
    ├── ValidateSensorReadingDelegate.java              # Front task
    ├── SendWarningEmailDelegate.java                   # WARNING path
    ├── ActivateEmergencyCoolingDelegate.java           # DANGER path — step 1
    ├── PageOnCallEngineerDelegate.java                 # DANGER path — step 2
    └── RecordAuditTrailDelegate.java                   # Back task

src/main/resources/
├── application.yaml                                    # Spring Boot + JPA + H2 config
└── datacenter-temperature-alert.bpmn                   # The process definition
```

**Architecture decisions:**
- **REST controller decouples callers from Camunda** — the IoT gateway calls a clean API, not Camunda internals
- **JPA entity persists audit records** — `AlertRecord` is a real database row, not just a log line
- **Delegates are thin** — they orchestrate calls to services, not contain business logic
- **Services simulate real integrations** — each has comments showing what the production API call would be
- **Domain model is serializable** — `SensorReading` is stored as a process variable and passed between tasks
- **Severity uses ASHRAE standards** — not arbitrary thresholds, based on real data center guidelines

---

## 🚀 Running the Example

### Step 1: Build and Start
```bash
mvn clean package
mvn spring-boot:run
```

### Step 2: Access the Application
| URL | Purpose |
|-----|---------|
| http://localhost:8080/camunda/app/cockpit | Camunda Cockpit (demo/demo) |
| http://localhost:8080/api/sensor/readings | POST — Submit sensor reading |
| http://localhost:8080/api/sensor/alerts | GET — Query alert history |
| http://localhost:8080/h2-console | H2 Database Console |

**H2 Console connection:** JDBC URL = `jdbc:h2:file:./camunda-h2-database`, User = `sa`, Password = *(empty)*

---

## 🧪 Test Scenarios

### Scenario 1: WARNING — Server room running warm (35°C)

A sensor in Hall B reports 35°C. This is above the 27°C ASHRAE threshold but below the 40°C danger zone. The ops team gets an email, and an audit record is persisted.

```bash
curl -X POST http://localhost:8080/api/sensor/readings \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "SENSOR-DC1-B2-07",
    "temperature": 35,
    "location": "Data Center 1 — Hall B, Row 2"
  }'
```

**Expected JSON response:**
```json
{
  "received": true,
  "temperature": 35,
  "sensorId": "SENSOR-DC1-B2-07",
  "alertTriggered": true,
  "processInstanceId": "12345",
  "severity": "WARNING"
}
```

**Expected log output:**
```
Received sensor reading: sensorId=SENSOR-DC1-B2-07, temperature=35°C, location=Data Center 1 — Hall B, Row 2
=== VALIDATE SENSOR READING ===
Severity classified: WARNING — Attention required — notify operations team via email
=== WARNING PATH: Send Email Alert ===
>>> EMAIL: Sending warning to dc-ops@company.com
=== RECORD AUDIT TRAIL ===
AUDIT: Persisted alert record AlertRecord{auditId='AUD-...', sensor='SENSOR-DC1-B2-07', temp=35°C, severity=WARNING, ...}
```

### Scenario 2: DANGER — CRAC unit failure, temperature spiking (48°C)

A CRAC unit in Hall A has failed. Temperature is spiking to 48°C. Emergency cooling is activated, the on-call engineer is paged, and the audit record is persisted.

```bash
curl -X POST http://localhost:8080/api/sensor/readings \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "SENSOR-DC1-A3-01",
    "temperature": 48,
    "location": "Data Center 1 — Hall A, Row 3"
  }'
```

**Expected JSON response:**
```json
{
  "received": true,
  "temperature": 48,
  "sensorId": "SENSOR-DC1-A3-01",
  "alertTriggered": true,
  "processInstanceId": "12346",
  "severity": "DANGER"
}
```

### Scenario 3: No trigger — Normal temperature (25°C)

```bash
curl -X POST http://localhost:8080/api/sensor/readings \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "SENSOR-DC2-C1-03",
    "temperature": 25
  }'
```

**Expected JSON response:**
```json
{
  "received": true,
  "temperature": 25,
  "sensorId": "SENSOR-DC2-C1-03",
  "alertTriggered": false,
  "reason": "Temperature 25°C is within normal range (≤27°C)"
}
```

### Scenario 4: Query alert history

After submitting readings, query the persisted alert records:

```bash
# All alerts
curl http://localhost:8080/api/sensor/alerts

# Filter by severity
curl http://localhost:8080/api/sensor/alerts?severity=DANGER
```

**Expected JSON response:**
```json
[
  {
    "id": 1,
    "auditId": "AUD-1710000000000",
    "sensorId": "SENSOR-DC1-A3-01",
    "location": "Data Center 1 — Hall A, Row 3",
    "rackId": "RACK-A3-01",
    "temperatureCelsius": 48,
    "severity": "DANGER",
    "actionTaken": "Emergency cooling activated (BMS: BMS-EMG-...), on-call paged (PD: PD-...)",
    "recordedAt": "2026-03-14T17:08:00Z",
    "processInstanceId": "12346"
  }
]
```

### Scenario 5: Inspect database directly

Open http://localhost:8080/h2-console and run:

```sql
SELECT * FROM ALERT_RECORDS ORDER BY RECORDED_AT DESC;
```

### Summary of Test Cases

| Temperature | Sensor ID | Path Taken | Key Actions |
|-------------|-----------|------------|-------------|
| 25°C | SENSOR-DC2-C1-03 | ❌ No start | Condition not met (25 ≤ 27) |
| 27°C | SENSOR-DC1-B2-07 | ❌ No start | Boundary: not > 27 |
| 35°C | SENSOR-DC1-B2-07 | ✅ WARNING | Email to ops team, audit record persisted |
| 38°C | *(none)* | ✅ WARNING | Graceful degradation, unregistered sensor |
| 41°C | SENSOR-DC1-A3-01 | ✅ DANGER | Emergency cooling + PagerDuty page + audit record |
| 48°C | SENSOR-DC1-A3-01 | ✅ DANGER | Emergency cooling + PagerDuty page + audit record |

---

## 📋 Condition Expression Reference

The conditional start uses ASHRAE TC 9.9 threshold (27°C upper recommended limit):

```xml
${temperature != null && temperature > 27}
```

The exclusive gateway routes by severity (set by the validation delegate):

```xml
<!-- WARNING path -->
${severity == 'WARNING'}

<!-- DANGER path -->
${severity == 'DANGER'}
```

### Other Condition Patterns (for reference)

```xml
<!-- Multiple conditions -->
${temperature > 27 && humidity > 80}

<!-- String comparison -->
${systemStatus == 'CRITICAL'}

<!-- Numeric threshold with null safety -->
${amount != null && amount > 10000}
```

---

## ⚠️ Camunda 7 vs Camunda 8

| Aspect | Camunda 7 (this example) | Camunda 8 |
|--------|--------------------------|-----------|
| Trigger mechanism | Explicit: `runtimeService.createConditionEvaluation()` or `POST /engine-rest/condition` | Automatic: engine monitors variable changes |
| Evaluation | On-demand only | Continuous |
| Best fit | When an external system pushes data | When variables change within running processes |
| Recommendation | Use Message Start Event + condition check in first task for production | Full conditional start support |

---

## 💡 Best Practices Demonstrated

1. **REST controller decouples callers from Camunda** — IoT gateway calls a clean API, not Camunda internals
2. **JPA persistence for audit records** — real database writes, not just log lines
3. **Front task validates before processing** — catches sensor malfunctions before taking action
4. **Exclusive gateway for severity routing** — real processes branch, they don't just run in a straight line
5. **Services are injected via constructor** — proper Spring DI, not field injection
6. **Delegates are thin orchestrators** — business logic lives in the service layer
7. **Domain model is serializable** — `SensorReading` travels between tasks as a process variable
8. **Severity uses industry standards** — ASHRAE TC 9.9, not arbitrary numbers
9. **BpmnError for validation failures** — allows error boundary events in production
10. **Audit trail closes the loop** — both paths converge to a single persisted audit record
11. **SLF4J logging throughout** — no `System.out.println`
12. **`historyTimeToLive` set on process** — required by Camunda for history cleanup

---

## 🔗 Official Documentation

- [Conditional Events](https://docs.camunda.org/manual/latest/reference/bpmn20/events/conditional-events/)
- [Expression Language (UEL)](https://docs.camunda.org/manual/latest/user-guide/process-engine/expression-language/)
- [Condition Evaluation REST API](https://docs.camunda.org/manual/latest/reference/rest/condition/)
- [Java Delegate](https://docs.camunda.org/manual/latest/user-guide/process-engine/delegation-code/#java-delegate)
- [Exclusive Gateway](https://docs.camunda.org/manual/latest/reference/bpmn20/gateways/exclusive-gateway/)

---

## 📚 Next Steps

- **[06-message-intermediate-catch](../06-message-intermediate-catch/)** — Wait for message during process
- **[07-timer-intermediate-catch](../07-timer-intermediate-catch/)** — Wait/delay during process
- **[09-conditional-intermediate-catch](../09-conditional-intermediate-catch/)** — Wait for condition during process
