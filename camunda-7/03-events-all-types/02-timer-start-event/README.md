# 02. Timer Start Event

## 📖 What is a Timer Start Event?

A **Timer Start Event** automatically starts a process instance based on a time schedule. It can trigger once at a specific time, after a duration, or repeatedly using a cycle.

**Key Characteristics:**
- ✅ Automatic process start (no manual trigger)
- ✅ Schedule-based execution
- ✅ Supports one-time, duration, and recurring timers
- ✅ Uses ISO 8601 format

---

## 🎯 Real-World Use Cases

### 1. Daily Batch Report Generation

**Scenario:** A financial institution generates end-of-day transaction reports at midnight for compliance and auditing.

**Timer:** `0 0 0 * * ?` (Cron: Daily at midnight)

**BPMN Flow:**
```
[⏰ Daily at Midnight] → [Fetch Daily Transactions] → [Generate Compliance Report] → [Send Report via Email] → [● Report Delivered]
```

**Process Variables Flow:**
| Step | Sets Variables |
|------|---------------|
| Fetch Transactions | `reportDate`, `transactionCount`, `totalAmount` |
| Generate Report | `reportId`, `reportStatus` |
| Send Email | `emailSent` |

---

### 2. Weekly Data Cleanup

**Scenario:** An e-commerce platform purges abandoned shopping carts (>30 days) and expired sessions every Sunday at 2 AM.

**Timer:** `0 0 2 ? * SUN` (Cron: Every Sunday at 02:00)

**BPMN Flow:**
```
[⏰ Every Sunday 2 AM] → [Identify Expired Data] → [Purge Expired Records] → [Log Cleanup Summary] → [● Cleanup Complete]
```

**Process Variables Flow:**
| Step | Sets Variables |
|------|---------------|
| Identify Expired Data | `expiredCartCount`, `expiredSessionCount`, `cleanupStartTime` |
| Purge Records | `totalPurgedRecords`, `purgeStatus` |
| Log Summary | `cleanupEndTime` |

---

### 3. Monthly Invoice Processing

**Scenario:** A SaaS company processes subscription invoices on the 1st of every month at 9 AM — calculates charges, generates invoices, and sends payment reminders.

**Timer:** `0 0 9 1 * ?` (Cron: 1st of month at 09:00)

**BPMN Flow:**
```
[⏰ 1st of Month 9 AM] → [Calculate Monthly Charges] → [Generate Invoice] → [Send Payment Reminder] → [● Invoice Sent]
```

**Process Variables Flow:**
| Step | Sets Variables |
|------|---------------|
| Calculate Charges | `billingPeriod`, `activeSubscriptions`, `totalCharges` |
| Generate Invoice | `invoiceBatchId`, `invoicesGenerated` |
| Send Reminder | `remindersSent`, `invoiceStatus` |

---

### 4. Hourly System Health Check

**Scenario:** A cloud monitoring service checks API endpoints, database connections, and service availability every hour.

**Timer:** `0 0 * * * ?` (Cron: Every hour)

**BPMN Flow:**
```
[⏰ Every Hour] → [Check API Endpoint Status] → [Check Database Status] → [Log Health Check Results] → [● Health Check Done]
```

**Process Variables Flow:**
| Step | Sets Variables |
|------|---------------|
| Check API Status | `checkTime`, `apiStatus`, `apiResponseTimeMs` |
| Check DB Status | `dbStatus`, `activeConnections`, `dbQueryTimeMs` |
| Log Results | `overallHealthStatus` |

---

### 5. Delayed Notification Trigger

**Scenario:** After a user signs up, wait 5 minutes for account activation to complete, then verify and send a welcome email.

**Timer:** `PT5M` (Duration: 5 minutes)

**BPMN Flow:**
```
[⏰ After 5 Minutes] → [Verify Account Status] → [Send Welcome Email] → [● Notification Sent]
```

**Process Variables Flow:**
| Step | Sets Variables |
|------|---------------|
| Verify Account | `accountActive`, `emailVerified`, `verificationTime` |
| Send Welcome Email | `welcomeEmailSent` |

---

## ⏱️ Timer Types

### 1. Time Date (Specific Time)
```xml
<bpmn:timerEventDefinition>
  <bpmn:timeDate>2024-12-31T23:59:59</bpmn:timeDate>
</bpmn:timerEventDefinition>
```

### 2. Time Duration (After Delay) — Used in Use Case 5
```xml
<bpmn:timerEventDefinition>
  <bpmn:timeDuration>PT5M</bpmn:timeDuration>
</bpmn:timerEventDefinition>
```

### 3. Time Cycle (Recurring) — Used in Use Cases 1–4
```xml
<bpmn:timerEventDefinition>
  <bpmn:timeCycle>0 0 0 * * ?</bpmn:timeCycle>
</bpmn:timerEventDefinition>
```

---

## 📁 Project Structure

```
02-timer-start-event/
├── src/main/
│   ├── java/com/example/event/
│   │   ├── Application.java
│   │   └── delegate/
│   │       ├── dailyreport/
│   │       │   ├── FetchTransactionsDelegate.java
│   │       │   ├── GenerateReportDelegate.java
│   │       │   └── SendReportEmailDelegate.java
│   │       ├── weeklycleanup/
│   │       │   ├── IdentifyExpiredDataDelegate.java
│   │       │   ├── PurgeRecordsDelegate.java
│   │       │   └── LogCleanupSummaryDelegate.java
│   │       ├── monthlyinvoice/
│   │       │   ├── CalculateChargesDelegate.java
│   │       │   ├── GenerateInvoiceDelegate.java
│   │       │   └── SendPaymentReminderDelegate.java
│   │       ├── healthcheck/
│   │       │   ├── CheckApiStatusDelegate.java
│   │       │   ├── CheckDbStatusDelegate.java
│   │       │   └── LogHealthResultsDelegate.java
│   │       └── notification/
│   │           ├── VerifyAccountStatusDelegate.java
│   │           └── SendWelcomeEmailDelegate.java
│   └── resources/
│       ├── application.yaml
│       ├── 01-daily-batch-report-generation.bpmn
│       ├── 02-weekly-data-cleanup.bpmn
│       ├── 03-monthly-invoice-processing.bpmn
│       ├── 04-hourly-system-health-check.bpmn
│       └── 05-delayed-notification-trigger.bpmn
├── pom.xml
└── README.md
```

---

## 🚀 Running the Examples

### Step 1: Build and Start
```bash
mvn clean package
mvn spring-boot:run
```

### Step 2: Watch Console Logs

**Daily Report (triggers at midnight):**
```
📥 Fetching transactions for date: 2024-01-14
   Found 1542 transactions to process
📊 Generating compliance report: RPT-A1B2C3D4
   Transactions: 1542 | Total Amount: $284750.75
📧 Sending report RPT-A1B2C3D4 via email
   Recipients: compliance-team@example.com, finance@example.com
```

**Weekly Cleanup (triggers Sunday 2 AM):**
```
🔍 Identifying expired data at: 2024-01-14 02:00:00
   Abandoned carts (>30 days): 237
   Expired sessions: 1893
🗑️ Purging expired records...
   Total records purged: 2130
📋 Weekly Cleanup Summary
   Status: COMPLETED SUCCESSFULLY
```

**Hourly Health Check (triggers every hour):**
```
🌐 Checking API endpoint status at: 2024-01-15 10:00:00
   Endpoint: /api/v1/health → HEALTHY (45ms)
🗄️ Checking database status...
   Connection Pool: 12/50 active
📋 Health Check Summary
   Overall Status: ALL_SYSTEMS_HEALTHY
```

### Step 3: Verify in Cockpit
Open: http://localhost:8080/camunda/app/cockpit  
Login: demo / demo

---

## 📊 Testing with Short Intervals

For quick testing, modify the timer in any BPMN:

```xml
<!-- Replace cron with short cycle -->
<bpmn:timeCycle>R3/PT30S</bpmn:timeCycle>
```
This runs 3 times, every 30 seconds — ideal for local testing.

---

## 🧪 Verification Commands

```bash
# Query running instances
curl "http://localhost:8080/engine-rest/process-instance?processDefinitionKey=daily-batch-report-generation"

# Count instances
curl "http://localhost:8080/engine-rest/process-instance/count?processDefinitionKey=hourly-system-health-check"

# View process history
curl "http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=monthly-invoice-processing"

# Check process variables (replace {id} with actual process instance ID)
curl "http://localhost:8080/engine-rest/history/variable-instance?processInstanceId={id}"
```

---

## 📅 Timer Configuration Reference

### ISO 8601 Duration
| Format | Meaning |
|--------|---------|
| `PT30S` | 30 seconds |
| `PT5M` | 5 minutes |
| `PT1H` | 1 hour |
| `P1D` | 1 day |
| `R/PT1M` | Repeat every 1 minute |
| `R5/PT10S` | Repeat 5 times, every 10 seconds |

### Cron Expressions
| Expression | Meaning |
|------------|---------|
| `0 0 0 * * ?` | Daily at midnight |
| `0 0 2 ? * SUN` | Every Sunday at 2 AM |
| `0 0 9 1 * ?` | 1st of month at 9 AM |
| `0 0 * * * ?` | Every hour |
| `0 0 9-17 * * MON-FRI` | Business hours weekdays |

**Format:** `second minute hour day month weekday`

---

## 💡 Best Practices

1. **Use process variables** to pass data between service tasks
2. **Test with short intervals** — Use `R3/PT30S` for local testing
3. **Monitor instances** — Check Cockpit to avoid runaway timers
4. **Use cron for complex schedules** — Business hours, specific days
5. **Consider time zones** — Server time zone affects timer execution
6. **Organize delegates by use case** — One package per process

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Timer not triggering | Check `job-execution.enabled: true` in application.yaml |
| Too many instances | Stop app, clean DB, use `R3/` prefix to limit repeats |
| Wrong time zone | Set JVM timezone: `-Duser.timezone=UTC` |
| Process variables null | Verify previous task sets the variable before current task reads it |

---

## 🔗 Official Documentation

- [Timer Events](https://docs.camunda.org/manual/latest/reference/bpmn20/events/timer-events/)
- [ISO 8601 Duration](https://en.wikipedia.org/wiki/ISO_8601#Durations)
- [Cron Expressions](https://docs.camunda.org/manual/latest/reference/bpmn20/events/timer-events/#cron-expressions)
- [Job Executor](https://docs.camunda.org/manual/latest/user-guide/process-engine/the-job-executor/)

---

## 📚 Next Steps

- **[01-none-start-event](../01-none-start-event/)** - Manual start events
- **[03-message-start-event](../03-message-start-event/)** - Message-triggered start
- **[07-timer-intermediate-catch](../07-timer-intermediate-catch/)** - Timer during process
