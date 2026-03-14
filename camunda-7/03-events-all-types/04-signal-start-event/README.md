# 04. Signal Start Event

## 📖 What is a Signal Start Event?

A **Signal Start Event** starts a process instance when a broadcast signal is received. Unlike messages (point-to-point), signals are broadcast to ALL listening processes.

**Key Characteristics:**
- ✅ Broadcast mechanism (one-to-many)
- ✅ Triggers ALL processes listening to the signal
- ✅ No correlation needed
- ✅ Global scope across process engine

---

## 🎯 Use Cases with Real-World Examples

This project demonstrates 5 real-world use cases across 3 distinct signals, plus a **signal broadcaster process** that shows how signals are sent from within a BPMN process.

Use cases 1–3 share the **same signal** (`EmergencyAlert`) to demonstrate the broadcast (one-to-many) nature of signals.

| # | Use Case | Real-World Scenario | Signal Name | Process Key |
|---|----------|---------------------|-------------|-------------|
| 🔴 | **Signal Broadcaster (Sender)** | Emergency reported → Validate → Broadcast signal | `EmergencyAlert` | `emergency-signal-broadcaster-process` |
| 1 | **Emergency Broadcast Notification** | Hospital Code Blue → ER preparation → Notify surgeon | `EmergencyAlert` | `emergency-er-preparation-process` |
| 2 | **Multi-Department Process Trigger** | Hospital Code Blue → Security lockdown → Log incident | `EmergencyAlert` | `emergency-security-lockdown-process` |
| 3 | **System-Wide Event Broadcasting** | Hospital Code Blue → Admin notification → Update dashboard | `EmergencyAlert` | `emergency-admin-notification-process` |
| 4 | **Automated System Maintenance** | Scheduled maintenance → Cache cleanup → Suspend health checks | `SystemMaintenanceActivated` | `system-maintenance-cleanup-process` |
| 5 | **Cascade Onboarding Trigger** | New employee hired → IT provisioning → Send welcome email | `EmployeeOnboarded` | `employee-it-provisioning-process` |

---

### Signal Broadcaster (Sender Process — The "Front Task")

**Scenario:** A nurse or doctor reports a **Code Blue** emergency. The system validates the emergency details (severity, location) and then broadcasts the `EmergencyAlert` signal via a **Signal End Event**. This is the "front task" — the process that *sends* the signal.

**Signal:** `EmergencyAlert`
**Process:** `emergency-signal-broadcaster-process`
**Delegate:** `ValidateEmergencyDelegate`

```
[● Emergency Reported] → [⚙️ Validate Emergency Details] → [📡 Broadcast Emergency Alert (Signal End Event)]
```

**BPMN File:** `emergency-signal-broadcaster-process.bpmn`

> **Key Learning:** This process uses a **None Start Event** (started manually or via REST) and ends with a **Signal End Event** that broadcasts `EmergencyAlert`. This is the Camunda-native way to send signals from within a BPMN process. The signal can also be sent externally via the REST API (`/engine-rest/signal`).

---

### Use Case 1: Emergency Broadcast Notification

**Scenario:** A hospital triggers a **Code Blue** (cardiac arrest). The ER department immediately prepares a trauma bay and readies life-saving equipment, then pages the on-call surgeon to report to the ER.

**Signal:** `EmergencyAlert`
**Process:** `emergency-er-preparation-process`
**Delegates:** `ErPreparationDelegate` → `NotifyOnCallSurgeonDelegate`

```
[📡 Emergency Alert Received] → [⚙️ Prepare ER for Incoming Patient] → [📟 Notify On-Call Surgeon] → [● ER Preparation Complete]
```

**BPMN File:** `emergency-er-preparation-process.bpmn`

---

### Use Case 2: Multi-Department Process Trigger

**Scenario:** The **same Code Blue** signal simultaneously triggers the security department to restrict visitor access, deploy security personnel to the affected wing, and lock the facility perimeter. After lockdown, the incident is logged in the security management system.

**Signal:** `EmergencyAlert` (same as Use Case 1)
**Process:** `emergency-security-lockdown-process`
**Delegates:** `SecurityLockdownDelegate` → `LogSecurityIncidentDelegate`

```
[📡 Emergency Alert Received] → [⚙️ Initiate Security Lockdown] → [📝 Log Security Incident] → [● Security Lockdown Activated]
```

**BPMN File:** `emergency-security-lockdown-process.bpmn`

> **Key Learning:** Use Cases 1 and 2 share the same signal name. One broadcast starts BOTH processes — this is the core broadcast behavior of signals.

---

### Use Case 3: System-Wide Event Broadcasting

**Scenario:** The **same Code Blue** signal also triggers hospital administration notification — paging the chief medical officer and alerting administrators. After notification, the incident dashboard is updated with active incident details for real-time monitoring.

**Signal:** `EmergencyAlert` (same as Use Cases 1 & 2)
**Process:** `emergency-admin-notification-process`
**Delegates:** `AdminNotificationDelegate` → `UpdateIncidentDashboardDelegate`

```
[📡 Emergency Alert Received] → [⚙️ Notify Hospital Administration] → [📊 Update Incident Dashboard] → [● Administration Notified]
```

**BPMN File:** `emergency-admin-notification-process.bpmn`

> **Key Learning:** One `EmergencyAlert` signal broadcast → 3 process instances created simultaneously (ER prep + Security lockdown + Admin notification). This is the **one-to-many** pattern.

---

### Use Case 4: Automated System Maintenance

**Scenario:** An e-commerce platform enters **scheduled maintenance mode**. The signal triggers automated cleanup — flushing application caches and clearing expired user sessions. After cleanup, external health check endpoints are suspended to avoid false alerts during the maintenance window.

**Signal:** `SystemMaintenanceActivated`
**Process:** `system-maintenance-cleanup-process`
**Delegates:** `SystemMaintenanceCleanupDelegate` → `SuspendHealthChecksDelegate`

```
[📡 Maintenance Mode Activated] → [⚙️ Perform System Cleanup] → [⏸️ Suspend Health Checks] → [● Maintenance Cleanup Complete]
```

**BPMN File:** `system-maintenance-cleanup-process.bpmn`

---

### Use Case 5: Cascade Onboarding Trigger

**Scenario:** HR completes a **new employee onboarding** in the HRIS system. The signal triggers IT provisioning — creating an email account, provisioning VPN access, assigning a laptop, and setting up Active Directory group memberships. After provisioning, a welcome email is sent to the new employee with login credentials and an onboarding guide.

**Signal:** `EmployeeOnboarded`
**Process:** `employee-it-provisioning-process`
**Delegates:** `EmployeeItProvisioningDelegate` → `SendWelcomeEmailDelegate`

```
[📡 Employee Onboarded] → [⚙️ Provision IT Access] → [📧 Send Welcome Email] → [● IT Provisioning Complete]
```

**BPMN File:** `employee-it-provisioning-process.bpmn`

> **Note:** In a real enterprise, this same `EmployeeOnboarded` signal could also trigger HR orientation scheduling, facilities badge provisioning, and payroll setup — each as separate processes listening to the same signal.

---

## 🏗️ Architecture Overview

### Complete Signal Flow (Sender → Receivers)

```
[● Emergency Reported]
        │
        ▼
[⚙️ Validate Emergency Details]
        │
        ▼
[📡 Signal End Event: EmergencyAlert]  ── broadcast ──┐
                                                       │
        ┌──────────────────────────────────────────────┤
        │                                              │
        ▼                                              ▼
[emergency-er-preparation-process]          [emergency-security-lockdown-process]
  📡 → ⚙️ Prepare ER → 📟 Notify Surgeon     📡 → ⚙️ Lockdown → 📝 Log Incident
        → ● Complete                                → ● Activated
        │
        ▼
[emergency-admin-notification-process]
  📡 → ⚙️ Notify Admin → 📊 Update Dashboard
        → ● Notified
```

### Independent Signal Flows (Use Cases 4–5)

```
[Signal: SystemMaintenanceActivated] ──→ 📡 → ⚙️ Cleanup → ⏸️ Suspend Health Checks → ● Complete

[Signal: EmployeeOnboarded]          ──→ 📡 → ⚙️ Provision IT → 📧 Send Welcome Email → ● Complete
```

---

## 🔧 Implementation

### Project Structure

```
src/main/
├── java/com/example/event/
│   ├── Application.java
│   └── delegate/
│       ├── ValidateEmergencyDelegate.java          ← Sender (front task)
│       ├── ErPreparationDelegate.java              ← Use Case 1
│       ├── NotifyOnCallSurgeonDelegate.java        ← Use Case 1 (downstream)
│       ├── SecurityLockdownDelegate.java           ← Use Case 2
│       ├── LogSecurityIncidentDelegate.java        ← Use Case 2 (downstream)
│       ├── AdminNotificationDelegate.java          ← Use Case 3
│       ├── UpdateIncidentDashboardDelegate.java    ← Use Case 3 (downstream)
│       ├── SystemMaintenanceCleanupDelegate.java   ← Use Case 4
│       ├── SuspendHealthChecksDelegate.java        ← Use Case 4 (downstream)
│       ├── EmployeeItProvisioningDelegate.java     ← Use Case 5
│       └── SendWelcomeEmailDelegate.java           ← Use Case 5 (downstream)
└── resources/
    ├── emergency-signal-broadcaster-process.bpmn   ← Sender process
    ├── emergency-er-preparation-process.bpmn
    ├── emergency-security-lockdown-process.bpmn
    ├── emergency-admin-notification-process.bpmn
    ├── system-maintenance-cleanup-process.bpmn
    ├── employee-it-provisioning-process.bpmn
    └── application.yaml
```

### BPMN Configuration — Signal Sender (Signal End Event)

```xml
<!-- Signal definition (global) -->
<bpmn:signal id="Signal_EmergencyAlert_Broadcaster" name="EmergencyAlert" />

<!-- None Start Event (started manually or via REST) -->
<bpmn:startEvent id="StartEvent_Broadcaster" name="Emergency Reported">
  <bpmn:outgoing>Flow_1</bpmn:outgoing>
</bpmn:startEvent>

<!-- Signal End Event (broadcasts the signal) -->
<bpmn:endEvent id="EndEvent_BroadcastSignal" name="Broadcast Emergency Alert">
  <bpmn:incoming>Flow_2</bpmn:incoming>
  <bpmn:signalEventDefinition signalRef="Signal_EmergencyAlert_Broadcaster" />
</bpmn:endEvent>
```

### BPMN Configuration — Signal Receiver (Signal Start Event)

```xml
<!-- Signal definition (global) -->
<bpmn:signal id="Signal_EmergencyAlert_ErPrep" name="EmergencyAlert" />

<!-- Signal Start Event (within process) -->
<bpmn:startEvent id="StartEvent_ErPrep" name="Emergency Alert Received">
  <bpmn:signalEventDefinition signalRef="Signal_EmergencyAlert_ErPrep" />
</bpmn:startEvent>
```

**Key Elements:**
- **Signal End Event (Sender)**: Broadcasts the signal when the sender process completes
- **Signal Start Event (Receiver)**: Starts a new process instance when the signal is received
- **Signal name matching**: Sender and receiver must use the exact same signal `name` attribute
- **historyTimeToLive**: Set to `P5D` on all processes (Camunda best practice)

---

## 🚀 Running the Example

### Step 1: Build and Start
```bash
mvn clean package
mvn spring-boot:run
```

### Step 2: Option A — Broadcast via Sender Process (Camunda-native)

Start the broadcaster process, which validates the emergency and broadcasts the signal via Signal End Event:

```bash
curl -X POST http://localhost:8080/engine-rest/process-definition/key/emergency-signal-broadcaster-process/start \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "severity": {"value": "CRITICAL", "type": "String"},
      "location": {"value": "Wing B - Room 204", "type": "String"}
    }
  }'
```

**Expected Console Output:**
```
🚨 [EMERGENCY BROADCASTER] Validating emergency details
   Process Instance ID: <sender-id>
   Severity: CRITICAL
   Location: Wing B - Room 204
   Status: Validated — broadcasting EmergencyAlert signal to all listeners

🏥 [ER PREPARATION] Emergency Alert received
   Process Instance ID: <id-1>
   ...
📟 [ER PREPARATION] Notifying on-call surgeon
   ...

🔒 [SECURITY LOCKDOWN] Emergency Alert received
   Process Instance ID: <id-2>
   ...
📝 [SECURITY LOCKDOWN] Logging security incident
   ...

📢 [ADMIN NOTIFICATION] Emergency Alert received
   Process Instance ID: <id-3>
   ...
📊 [ADMIN NOTIFICATION] Updating incident dashboard
   ...
```

> **One sender process → Signal End Event → Three receiver process instances.** This is the full Camunda-to-Camunda signal flow.

### Step 2: Option B — Broadcast via REST API (External trigger)

```bash
curl -X POST http://localhost:8080/engine-rest/signal \
  -H "Content-Type: application/json" \
  -d '{
    "name": "EmergencyAlert",
    "variables": {
      "severity": {"value": "CRITICAL", "type": "String"},
      "location": {"value": "Wing B - Room 204", "type": "String"}
    }
  }'
```

> **Note:** This skips the sender process and directly broadcasts the signal. Use this when the signal originates from an external system (not from within a Camunda process).

### Step 3: Broadcast System Maintenance Signal
```bash
curl -X POST http://localhost:8080/engine-rest/signal \
  -H "Content-Type: application/json" \
  -d '{
    "name": "SystemMaintenanceActivated",
    "variables": {
      "maintenanceWindow": {"value": "2025-01-15T02:00-04:00", "type": "String"},
      "initiatedBy": {"value": "ops-team", "type": "String"}
    }
  }'
```

**Expected Console Output:**
```
🔧 [SYSTEM MAINTENANCE] Maintenance mode activated
   Process Instance ID: <id-4>
   Maintenance Window: 2025-01-15T02:00-04:00
   Initiated By: ops-team
   Action: Flushing caches, clearing expired sessions, suspending health checks

⏸️ [SYSTEM MAINTENANCE] Suspending external health checks
   Process Instance ID: <id-4>
   ...
   Status: Health check endpoints suspended to avoid false alerts
```

### Step 4: Broadcast Employee Onboarded Signal
```bash
curl -X POST http://localhost:8080/engine-rest/signal \
  -H "Content-Type: application/json" \
  -d '{
    "name": "EmployeeOnboarded",
    "variables": {
      "employeeName": {"value": "Jane Smith", "type": "String"},
      "department": {"value": "Engineering", "type": "String"}
    }
  }'
```

**Expected Console Output:**
```
💻 [IT PROVISIONING] New employee onboarded
   Process Instance ID: <id-5>
   Employee: Jane Smith
   Department: Engineering
   Action: Creating email account, provisioning VPN access, assigning laptop, setting up AD groups

📧 [IT PROVISIONING] Sending welcome email
   Process Instance ID: <id-5>
   ...
   Status: Welcome email sent with login credentials and onboarding guide
```

### Step 5: Verify in Cockpit
Open: http://localhost:8080/camunda/app/cockpit
Login: demo / demo

---

## 📊 Signal vs Message Comparison

| Feature | Signal | Message |
|---------|--------|---------|
| **Communication** | Broadcast (1-to-many) | Point-to-point (1-to-1) |
| **Scope** | Global | Targeted |
| **Correlation** | Not needed | Required |
| **Recipients** | All listeners | Specific process |
| **Use Case** | System-wide events | Specific notifications |

---

## 🧪 Verification Commands

### Query Broadcaster Process Instances
```bash
curl http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=emergency-signal-broadcaster-process
```

### Query Emergency Process Instances (All 3 Receiver Processes)
```bash
curl http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=emergency-er-preparation-process
curl http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=emergency-security-lockdown-process
curl http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=emergency-admin-notification-process
```

### Query Maintenance Process Instances
```bash
curl http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=system-maintenance-cleanup-process
```

### Query IT Provisioning Process Instances
```bash
curl http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=employee-it-provisioning-process
```

---

## 📋 BPMN Changes Summary

| What Changed | Before | After |
|---|---|---|
| **Sender process** | None (REST API only) | `emergency-signal-broadcaster-process.bpmn` with Signal End Event |
| **BPMN files** | 1 generic (`signal-start-event-process.bpmn`) | 6 use-case-specific BPMNs (1 sender + 5 receivers) |
| **Delegates** | 1 generic (`LoggerDelegate`) | 11 domain-specific delegates in `delegate` package |
| **Downstream tasks** | None (single task per process) | Each receiver has 2 service tasks (action + follow-up) |
| **Signal names** | `EmergencyAlert` only | `EmergencyAlert`, `SystemMaintenanceActivated`, `EmployeeOnboarded` |
| **historyTimeToLive** | Missing | Set to `P5D` on all processes |
| **Broadcast demo** | Not demonstrated | 3 processes share same signal to show 1-to-many |
| **Process variables** | Not used | Each delegate reads domain-specific variables |

---

## 📋 When to Use Signal vs Message

### Use Signal When:
- ✅ Need to notify multiple processes simultaneously
- ✅ Broadcasting system-wide events (emergency, maintenance)
- ✅ No specific target needed
- ✅ All listeners should react independently

### Use Message When:
- ✅ Need point-to-point communication
- ✅ Targeting a specific process instance
- ✅ Correlation is important
- ✅ Only one recipient should react

---

## 🎓 Key Concepts Learned

✅ **Signal Start Event** — Broadcast-triggered process start (receiver side)
✅ **Signal End Event** — Broadcast signal from within a BPMN process (sender side)
✅ **Broadcast Communication** — One signal starts multiple processes (Use Cases 1–3)
✅ **Global Scope** — Signals reach all listeners across the engine
✅ **No Correlation** — No targeting or correlation key needed
✅ **Process Variables** — Signals can carry context data to all started processes
✅ **Downstream Tasks** — Real-world processes have follow-up work after the initial signal reaction
✅ **REST API** — Broadcasting signals via `/engine-rest/signal` (external trigger)
✅ **historyTimeToLive** — Required Camunda best practice on all process definitions

---

## 💡 Best Practices

1. **Use descriptive signal names** — `EmergencyAlert` not `signal1`
2. **Document signal contracts** — What variables each signal carries
3. **Consider scope** — Signals are global; every listener will react
4. **Pass relevant variables** — Include context data with the signal
5. **Monitor broadcasts** — Track how many processes each signal starts
6. **Use for system events** — Not for targeted point-to-point communication
7. **Set historyTimeToLive** — Required on all process definitions in Camunda 7
8. **Use Signal End Event for senders** — Prefer Camunda-native signal broadcasting over REST API when the trigger originates from a process

---

## 🐛 Troubleshooting

### Issue: Signal not starting process
**Solution:** Ensure signal name matches exactly (case-sensitive): `EmergencyAlert`, `SystemMaintenanceActivated`, `EmployeeOnboarded`

### Issue: Too many instances created
**Explanation:** Each signal broadcast creates one instance per listening process definition. Broadcasting `EmergencyAlert` creates 3 instances (expected behavior). If using the sender process, the sender itself also creates 1 instance (4 total).

### Issue: Wrong process started
**Solution:** Check signal name — signals are global and trigger ALL listeners with that signal name.

### Issue: Variables not available in delegate
**Solution:** Ensure variables are passed in the signal REST API payload under the `variables` field, or set on the sender process before the Signal End Event.

### Issue: Sender process variables not reaching receiver processes
**Explanation:** Signal End Events in Camunda 7 broadcast the signal but do **not** automatically pass process variables to receiver processes. Use the REST API with `variables` if you need to pass data, or use process-level signal variable mapping.

---

## 🔗 Official Documentation

- [Signal Events](https://docs.camunda.org/manual/latest/reference/bpmn20/events/signal-events/)
- [Signal API](https://docs.camunda.org/manual/latest/reference/rest/signal/post-signal/)
- [Signal vs Message](https://docs.camunda.org/manual/latest/reference/bpmn20/events/signal-events/#signals-vs-messages)

---

## 📚 Next Steps

- **[03-message-start-event](../03-message-start-event/)** — Compare with message events (point-to-point)
- **[08-signal-intermediate-catch](../08-signal-intermediate-catch/)** — Wait for signal during process execution
- **[12-signal-intermediate-throw](../12-signal-intermediate-throw/)** — Broadcast signal during process execution
