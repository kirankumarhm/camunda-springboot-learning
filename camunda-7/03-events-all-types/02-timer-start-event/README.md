# 02. Timer Start Event

## What is a Timer Start Event?

A **Timer Start Event** automatically starts a process instance based on a time schedule. It can trigger once at a specific time, after a duration, or repeatedly using a cycle.

**Key Characteristics:**
- Automatic process start (no manual trigger needed)
- Schedule-based execution
- Supports one-time, duration, and recurring timers
- Uses ISO 8601 format for durations and cron expressions for cycles
- No task exists before the timer — it IS the process entry point

**Important:** A Timer Start Event is different from a Timer Intermediate Catch Event. The start event creates a new process instance when it fires. There is no "front task" — the timer replaces the manual trigger entirely.

---

## Real-World Use Cases

### Use Case 1: Daily Transaction Report (Financial Compliance)

**Industry:** Banking / Financial Services

**Real-World Scenario:** A retail bank must generate end-of-day transaction reports
for regulatory compliance (PCI-DSS, SOX, AML). Every night at midnight, the system
pulls all settled transactions from the core banking ledger, compiles them into a
compliance report, and emails it to the compliance team and external auditors.
Reports are archived for 5-7 years per SOX/AML retention requirements.

**Timer Type:** `timeCycle` (Cron)
**Schedule:** `0 0 0 * * ?` — Daily at midnight
**Process ID:** `timer-daily-transaction-report`

**BPMN Flow:**
```
[⏰ Daily at Midnight] → [Fetch Daily Transactions] → [Generate Compliance Report] → [Send Report via Email] → [● Report Delivered]
```

**Process Variables:**

| Step | Variables Set | Description |
|------|--------------|-------------|
| Fetch Transactions | `reportDate`, `transactionCount`, `totalAmount` | Previous day's settled transaction data |
| Generate Report | `reportId`, `reportStatus` | Unique report ID (e.g., RPT-A1B2C3D4) and generation status |
| Send Email | `emailSent`, `reportStatus` | Delivery confirmation; status updated to DELIVERED |

**Real-World Examples:**
- JPMorgan Chase generating daily AML transaction monitoring reports
- Stripe producing daily settlement reports for connected accounts
- PayPal compiling daily cross-border transaction summaries for regulators

---

### Use Case 2: Weekly Data Cleanup (E-Commerce Data Retention)

**Industry:** E-Commerce / Retail

**Real-World Scenario:** An e-commerce platform accumulates abandoned shopping carts
(users who added items but never checked out) and expired HTTP sessions. GDPR and
data retention policies require periodic purging of stale personal data. Every Sunday
at 2 AM (low-traffic window), the system scans for carts older than 30 days and
expired sessions, performs batch deletes, and logs the cleanup metrics for the ops dashboard.

**Timer Type:** `timeCycle` (Cron)
**Schedule:** `0 0 2 ? * SUN` — Every Sunday at 02:00
**Process ID:** `timer-weekly-data-cleanup`

**BPMN Flow:**
```
[⏰ Every Sunday 2 AM] → [Identify Expired Data] → [Purge Expired Records] → [Log Cleanup Summary] → [● Cleanup Complete]
```

**Process Variables:**

| Step | Variables Set | Description |
|------|--------------|-------------|
| Identify Expired Data | `expiredCartCount`, `expiredSessionCount`, `cleanupStartTime` | Counts of stale records found |
| Purge Records | `totalPurgedRecords`, `purgeStatus` | Total deleted records and operation status |
| Log Summary | `cleanupEndTime` | Completion timestamp for audit trail |

**Real-World Examples:**
- Amazon purging abandoned carts and expired wish lists weekly
- Shopify cleaning up expired checkout sessions for GDPR compliance
- eBay removing stale saved-search data and expired watch-list entries

---

### Use Case 3: Monthly Invoice Processing (SaaS Billing)

**Industry:** SaaS / Subscription Services

**Real-World Scenario:** A SaaS company (e.g., a project management tool) bills
customers monthly based on their subscription tier and usage metrics. On the 1st of
every month at 9 AM, the system aggregates usage data, applies pricing tiers and
volume discounts, generates PDF invoices with line items and tax calculations, and
sends payment reminder emails with secure payment links.

**Timer Type:** `timeCycle` (Cron)
**Schedule:** `0 0 9 1 * ?` — 1st of every month at 09:00
**Process ID:** `timer-monthly-invoice-processing`

**BPMN Flow:**
```
[⏰ 1st of Month 9 AM] → [Calculate Monthly Charges] → [Generate Invoice] → [Send Payment Reminder] → [● Invoice Sent]
```

**Process Variables:**

| Step | Variables Set | Description |
|------|--------------|-------------|
| Calculate Charges | `billingPeriod`, `activeSubscriptions`, `totalCharges` | Billing month, customer count, aggregate amount |
| Generate Invoice | `invoiceBatchId`, `invoicesGenerated` | Batch ID for reconciliation, invoice count |
| Send Reminder | `remindersSent`, `invoiceStatus` | Email count sent, status updated to REMINDERS_SENT |

**Real-World Examples:**
- Atlassian billing Jira/Confluence customers on the 1st of each month
- Slack generating per-workspace invoices based on active seat count
- HubSpot processing tiered subscription charges with usage overages

---

### Use Case 4: Hourly System Health Check (Infrastructure Monitoring)

**Industry:** Cloud Services / DevOps

**Real-World Scenario:** A cloud monitoring service performs periodic health checks
against critical API endpoints and database connections. Every hour, the system sends
HTTP requests to each endpoint, measures response latency, checks database connection
pool utilization, and aggregates results into an overall health status. If any component
is degraded, alerts are triggered via PagerDuty/OpsGenie.

**Timer Type:** `timeCycle` (Cron)
**Schedule:** `0 0 * * * ?` — Every hour on the hour
**Process ID:** `timer-hourly-system-health-check`

**BPMN Flow:**
```
[⏰ Every Hour] → [Check API Endpoint Status] → [Check Database Status] → [Log Health Check Results] → [● Health Check Done]
```

**Process Variables:**

| Step | Variables Set | Description |
|------|--------------|-------------|
| Check API Status | `checkTime`, `apiStatus`, `apiResponseTimeMs` | Timestamp, health status, average latency |
| Check DB Status | `dbStatus`, `activeConnections`, `dbQueryTimeMs` | Pool utilization and query latency |
| Log Results | `overallHealthStatus` | ALL_SYSTEMS_HEALTHY or DEGRADED |

**Real-World Examples:**
- Datadog Synthetics running hourly uptime checks on customer-facing APIs
- PagerDuty monitoring service health and auto-escalating incidents
- Pingdom performing scheduled HTTP checks with latency thresholds

---

### Use Case 5: Delayed Welcome Notification (User Onboarding)

**Industry:** SaaS / Consumer Apps

**Real-World Scenario:** After a user signs up on a platform, there is a 5-minute
grace period for email verification and account provisioning to complete. Once the
delay expires, the system verifies the account is active and the email is confirmed,
then sends a personalized welcome email with a getting-started guide, documentation
links, and a CTA to complete their profile.

**Timer Type:** `timeDuration` (ISO 8601 Duration)
**Schedule:** `PT5M` — 5 minutes after deployment/creation
**Process ID:** `timer-delayed-welcome-notification`

**Note:** This use case demonstrates `timeDuration` instead of `timeCycle`. The timer
fires exactly once after the specified delay. In production, this process would
typically be started programmatically (via REST API or message) after user signup,
not via a timer start event. The timer start event here is used for learning purposes
to demonstrate the duration timer type.

**BPMN Flow:**
```
[⏰ After 5 Minutes] → [Verify Account Status] → [Send Welcome Email] → [● Notification Sent]
```

**Process Variables:**

| Step | Variables Set | Description |
|------|--------------|-------------|
| Verify Account | `accountActive`, `emailVerified`, `verificationTime` | Account readiness check |
| Send Welcome Email | `welcomeEmailSent` | true if sent, false if account not ready |

**Real-World Examples:**
- Spotify sending a welcome email 5 minutes after signup with playlist suggestions
- GitHub sending onboarding tips after a new developer creates an account
- Notion sending a getting-started guide after workspace creation

---

## Timer Types Reference

### 1. Time Cycle (Recurring) — Used in Use Cases 1-4
```xml
<bpmn:timerEventDefinition>
  <bpmn:timeCycle>0 0 0 * * ?</bpmn:timeCycle>
</bpmn:timerEventDefinition>
```
Creates a new process instance every time the cron expression fires.

### 2. Time Duration (One-Time Delay) — Used in Use Case 5
```xml
<bpmn:timerEventDefinition>
  <bpmn:timeDuration>PT5M</bpmn:timeDuration>
</bpmn:timerEventDefinition>
```
Creates exactly one process instance after the specified delay from deployment.

### 3. Time Date (Specific Point in Time)
```xml
<bpmn:timerEventDefinition>
  <bpmn:timeDate>2026-12-31T23:59:59</bpmn:timeDate>
</bpmn:timerEventDefinition>
```
Creates exactly one process instance at the specified date/time.

---

## Why There Is No Task Before a Timer Start Event

A Timer Start Event is the entry point of a BPMN process. The process instance does
not exist until the timer fires. This is fundamentally different from:

- **Timer Intermediate Catch Event** — sits between two tasks: `Task A → ⏰ Wait → Task B`
- **Timer Boundary Event** — attached to a task, fires while the task is active

```
Timer Start Event (this project):
  [⏰ Timer] → [Task 1] → [Task 2] → [● End]
   ↑ Nothing before — the timer creates the process instance

Timer Intermediate Catch Event (different concept):
  [○ Start] → [Task A] → [⏰ Wait 2h] → [Task B] → [● End]
   ↑ Process already running — timer pauses execution
```

---

## Naming Convention

All artifacts follow a consistent naming convention:

| Artifact | Convention | Example |
|----------|-----------|---------|
| BPMN file | `timer-{description}.bpmn` | `timer-daily-transaction-report.bpmn` |
| Process ID | `timer-{description}` (kebab-case) | `timer-daily-transaction-report` |
| Process name | `Timer - {Description}` (title case) | `Timer - Daily Transaction Report` |
| Start event ID | `TimerStartEvent_{Context}` (PascalCase) | `TimerStartEvent_DailyMidnight` |
| Service task ID | `ServiceTask_{Action}` (PascalCase) | `ServiceTask_FetchDailyTransactions` |
| End event ID | `EndEvent_{Outcome}` (PascalCase) | `EndEvent_ReportDelivered` |
| Sequence flow ID | `SequenceFlow_{Source}To{Target}` | `SequenceFlow_StartToFetchTransactions` |
| Timer definition ID | `TimerEventDef_{Context}` | `TimerEventDef_DailyMidnight` |
| Java delegate bean | `camelCase` matching the action | `fetchTransactionsDelegate` |
| Java package | `delegate.{usecase}` (lowercase) | `delegate.dailyreport` |

---

## Project Structure

```
src/main/
├── java/com/example/event/
│   ├── Application.java
│   └── delegate/
│       ├── dailyreport/
│       │   ├── FetchTransactionsDelegate.java
│       │   ├── GenerateReportDelegate.java
│       │   └── SendReportEmailDelegate.java
│       ├── weeklycleanup/
│       │   ├── IdentifyExpiredDataDelegate.java
│       │   ├── PurgeRecordsDelegate.java
│       │   └── LogCleanupSummaryDelegate.java
│       ├── monthlyinvoice/
│       │   ├── CalculateChargesDelegate.java
│       │   ├── GenerateInvoiceDelegate.java
│       │   └── SendPaymentReminderDelegate.java
│       ├── healthcheck/
│       │   ├── CheckApiStatusDelegate.java
│       │   ├── CheckDbStatusDelegate.java
│       │   └── LogHealthResultsDelegate.java
│       └── notification/
│           ├── VerifyAccountStatusDelegate.java
│           └── SendWelcomeEmailDelegate.java
└── resources/
    ├── application.yaml
    ├── timer-daily-transaction-report.bpmn
    ├── timer-weekly-data-cleanup.bpmn
    ├── timer-monthly-invoice-processing.bpmn
    ├── timer-hourly-system-health-check.bpmn
    └── timer-delayed-welcome-notification.bpmn
```

---

## Running the Examples

### Step 1: Build and Start
```bash
mvn clean package
mvn spring-boot:run
```

### Step 2: Watch Console Logs

**Daily Report (triggers at midnight):**
```
Fetching transactions for date: 2026-03-14
  Found 1847 settled transactions totaling $327491.50
Generating compliance report: RPT-A1B2C3D4
  Report Date: 2026-03-14 | Transactions: 1847 | Total: $327491.50
Sending report RPT-A1B2C3D4 via email
  Recipients: compliance-team@example.com, finance@example.com
  Subject: Daily Transaction Report - 2026-03-14
```

**Weekly Cleanup (triggers Sunday 2 AM):**
```
Identifying expired data at: 2026-03-15 02:00:00
  Abandoned carts (>30 days): 312
  Expired sessions: 2847
Purging expired records...
  Deleted 312 abandoned carts
  Deleted 2847 expired sessions
  Total records purged: 3159
Weekly Cleanup Summary
  Start Time: 2026-03-15 02:00:00
  End Time: 2026-03-15 02:00:01
  Total Records Purged: 3159
  Status: COMPLETED SUCCESSFULLY
```

**Hourly Health Check (triggers every hour):**
```
Checking API endpoint status at: 2026-03-15 10:00:00
  /api/v1/health  -> 200 OK (42ms)
  /api/v1/orders  -> 200 OK (78ms)
  /api/v1/users   -> 200 OK (35ms)
  Average response time: 51ms | Status: HEALTHY
Checking database status...
  Connection Pool: 14/50 active
  Query Response Time: 6ms
  Database Status: HEALTHY
Health Check Summary at: 2026-03-15 10:00:00
  API Status:      HEALTHY
  Database Status:  HEALTHY
  Overall Status:   ALL_SYSTEMS_HEALTHY
```

### Step 3: Verify in Cockpit
Open: http://localhost:8080/camunda/app/cockpit
Login: demo / demo

---

## Testing with Short Intervals

For quick testing, modify the timer in any BPMN file:

```xml
<!-- Replace cron with short repeating cycle -->
<bpmn:timeCycle>R3/PT30S</bpmn:timeCycle>
```
This runs 3 times, every 30 seconds — ideal for local testing.

---

## Verification Commands

```bash
# Query running instances
curl "http://localhost:8080/engine-rest/process-instance?processDefinitionKey=timer-daily-transaction-report"

# Count instances
curl "http://localhost:8080/engine-rest/process-instance/count?processDefinitionKey=timer-hourly-system-health-check"

# View process history
curl "http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=timer-monthly-invoice-processing"

# Check process variables (replace {id} with actual process instance ID)
curl "http://localhost:8080/engine-rest/history/variable-instance?processInstanceId={id}"
```

---

## Timer Configuration Reference

### ISO 8601 Duration
| Format | Meaning |
|--------|---------|
| `PT30S` | 30 seconds |
| `PT5M` | 5 minutes |
| `PT1H` | 1 hour |
| `P1D` | 1 day |
| `R/PT1M` | Repeat every 1 minute (infinite) |
| `R5/PT10S` | Repeat 5 times, every 10 seconds |

### Cron Expressions
| Expression | Meaning |
|------------|---------|
| `0 0 0 * * ?` | Daily at midnight |
| `0 0 2 ? * SUN` | Every Sunday at 2 AM |
| `0 0 9 1 * ?` | 1st of month at 9 AM |
| `0 0 * * * ?` | Every hour |
| `0 0 9-17 * * MON-FRI` | Business hours weekdays |

**Cron format:** `second minute hour day month weekday`

---

## Best Practices

1. **Use process variables** to pass data between service tasks
2. **Test with short intervals** — Use `R3/PT30S` for local testing
3. **Monitor instances** — Check Cockpit to avoid runaway timers
4. **Use cron for complex schedules** — Business hours, specific days
5. **Consider time zones** — Server time zone affects timer execution
6. **Organize delegates by use case** — One package per process
7. **Use descriptive BPMN element IDs** — `ServiceTask_FetchDailyTransactions` not `Task_1`
8. **Add `camunda:versionTag`** to processes for deployment tracking
9. **Set `historyTimeToLive`** to prevent unbounded history growth

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Timer not triggering | Check `job-execution.enabled: true` in application.yaml |
| Too many instances | Stop app, clean DB, use `R3/` prefix to limit repeats |
| Wrong time zone | Set JVM timezone: `-Duser.timezone=UTC` |
| Process variables null | Verify previous task sets the variable before current task reads it |
| Old BPMN still deploying | Delete `camunda-h2-database.mv.db` and restart |

---

## Official Documentation

- [Timer Events](https://docs.camunda.org/manual/latest/reference/bpmn20/events/timer-events/)
- [ISO 8601 Duration](https://en.wikipedia.org/wiki/ISO_8601#Durations)
- [Cron Expressions](https://docs.camunda.org/manual/latest/reference/bpmn20/events/timer-events/#cron-expressions)
- [Job Executor](https://docs.camunda.org/manual/latest/user-guide/process-engine/the-job-executor/)

---

## Next Steps

- **[01-none-start-event](../01-none-start-event/)** — Manual start events
- **[03-message-start-event](../03-message-start-event/)** — Message-triggered start
- **[07-timer-intermediate-catch](../07-timer-intermediate-catch/)** — Timer during process
