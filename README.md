# camunda-springboot-learning
Learning Camunda With Springboot 3.5.5
# Camunda BPM Complete Learning Repository

## 🎯 Objective
Master EVERY Camunda BPM concept with working examples - from basic BPMN elements to advanced integrations (REST API, Kafka, External Systems).

---

## 📚 Complete Learning Path - Camunda 7 Community Edition

### 🔷 Phase 1: BPMN Tasks (Week 1)
**[01. Tasks - All Types](camunda-7/01-tasks-all-types/)** ⭐ START HERE
- Service Task (Java Delegate, Expression, Delegate Expression)
- User Task (Forms, Assignments, Listeners)
- Script Task (Groovy, JavaScript, Python)
- Business Rule Task (DMN Integration)
- Send Task (Message Sending)
- Receive Task (Message Receiving)
- Manual Task (Documentation)
- Call Activity (Reusable Processes)

### 🔶 Phase 2: BPMN Gateways (Week 1-2)
**[02. Gateways - All Types](camunda-7/02-gateways-all-types/)**
- Exclusive Gateway (XOR) - Choose ONE path
- Parallel Gateway (AND) - Execute ALL paths
- Inclusive Gateway (OR) - Execute SOME paths
- Event-Based Gateway - Wait for events
- Complex Gateway (Advanced conditions)

### 🔴 Phase 3: BPMN Events (Week 2-3)
**[03. Events - All Types](camunda-7/03-events-all-types/)**

**Start Events:**
- None Start Event
- Timer Start Event (Scheduled processes)
- Message Start Event (External trigger)
- Signal Start Event (Broadcast trigger)
- Conditional Start Event
- Error Start Event (Event Subprocess)
- Escalation Start Event (Event Subprocess)
- Compensation Start Event (Event Subprocess)

**End Events:**
- None End Event
- Terminate End Event (Kill all tokens)
- Error End Event (Throw error)
- Escalation End Event
- Cancel End Event (Transaction)
- Compensation End Event
- Signal End Event (Broadcast)
- Message End Event

**Intermediate Events:**
- Intermediate Throw Events (Signal, Message, Escalation, Compensation, Link)
- Intermediate Catch Events (Timer, Message, Signal, Conditional, Link)

**Boundary Events:**
- Interrupting (Timer, Message, Signal, Error, Escalation, Conditional, Cancel, Compensation)
- Non-Interrupting (Timer, Message, Signal, Escalation, Conditional)

### 🟣 Phase 4: BPMN Subprocesses (Week 3-4)
**[04. Subprocesses - All Types](camunda-7/04-subprocesses-all-types/)**
- Embedded Subprocess (Scope isolation)
- Event Subprocess (Exception handling)
- Transaction Subprocess (ACID operations)
- Call Activity (Reusable subprocess)
- Ad-Hoc Subprocess (Dynamic execution)

### 🟢 Phase 5: DMN Decisions (Week 4)
**[05. DMN Decisions](camunda-7/05-dmn-decisions/)**
- Decision Tables (Hit Policies: UNIQUE, FIRST, PRIORITY, ANY, COLLECT)
- Decision Requirements Diagrams (DRD)
- FEEL Expressions
- Input/Output Mappings
- DMN from BPMN (Business Rule Task)

### 🟡 Phase 6: Process Variables (Week 5)
**[06. Process Variables](camunda-7/06-process-variables/)**
- Variable Scopes (Process, Execution, Task, Local)
- Variable Types (Primitives, Objects, JSON, XML)
- Serialization (Java, JSON, XML)
- Transient Variables
- Input/Output Mappings
- Variable Listeners

### 🔵 Phase 7: Error Handling (Week 5-6)
**[07. Error Handling](camunda-7/07-error-handling/)**
- Error Events (Boundary, End, Start)
- Error Codes and Propagation
- Compensation Events
- Retry Mechanisms (Failed Jobs)
- Incident Management
- Fallback Strategies

### 🟠 Phase 8: Message Correlation (Week 6)
**[08. Message Correlation](camunda-7/08-message-correlation/)**
- Message Start Events
- Message Intermediate Catch Events
- Message Boundary Events
- Correlation Keys
- Message Correlation API
- Multiple Process Instances

### ⏰ Phase 9: Timers & Jobs (Week 7)
**[09. Timers and Jobs](camunda-7/09-timers-jobs/)**
- Timer Start Events (Cron, Cycle, Date)
- Timer Boundary Events (Interrupting/Non-interrupting)
- Timer Intermediate Catch Events
- Job Executor Configuration
- Async Continuations (Before/After)
- Job Priorities
- Failed Job Retry

### 🌐 Phase 10: REST API Integration (Week 7-8)
**[10. REST API Integration](camunda-7/10-rest-api-integration/)**
- Start Process Instances
- Query Process Instances
- Complete User Tasks
- Set/Get Variables
- Send Messages
- Handle External Tasks
- Deploy Processes
- Query History

### 📡 Phase 11: Kafka Integration (Week 8)
**[11. Kafka Integration](camunda-7/11-kafka-integration/)**
- Kafka Producer (Send messages from process)
- Kafka Consumer (Trigger process from Kafka)
- Message Correlation with Kafka
- Event-Driven Architecture
- Saga Pattern with Kafka

### 🔌 Phase 12: External Tasks (Week 9)
**[12. External Tasks](camunda-7/12-external-tasks/)**
- External Task Pattern
- External Task Workers (Java, Node.js, Python)
- Topic-Based Routing
- Long Polling
- Error Handling
- Retry Configuration
- Priority Handling

### 🔁 Phase 13: Multi-Instance (Week 9)
**[13. Multi-Instance](camunda-7/13-multi-instance/)**
- Sequential Multi-Instance (Loop)
- Parallel Multi-Instance (Fork-Join)
- Collection Variables
- Loop Cardinality
- Completion Conditions
- Element Variables

### ↩️ Phase 14: Compensation (Week 10)
**[14. Compensation](camunda-7/14-compensation/)**
- Compensation Events
- Compensation Handlers
- Compensation Boundary Events
- Compensation Throw Events
- Compensation Subprocess

### 💳 Phase 15: Transaction Subprocess (Week 10)
**[15. Transaction Subprocess](camunda-7/15-transaction-subprocess/)**
- Transaction Boundaries
- Commit/Rollback
- Cancel Events
- Compensation in Transactions
- ACID Properties

---

## 🚀 Quick Start

### Prerequisites
```bash
Java 21+
Maven 3.6+
Docker (for Kafka examples)
```

### Run Any Example
```bash
cd camunda-7/01-tasks-all-types
mvn spring-boot:run
```

### Access Camunda Webapps
```
Cockpit:  http://localhost:8080/camunda/app/cockpit
Tasklist: http://localhost:8080/camunda/app/tasklist
Admin:    http://localhost:8080/camunda/app/admin

Username: demo
Password: demo
```

---

## 📖 What Each Topic Includes

Every topic folder contains:
- ✅ **README.md** - Complete concept explanation
  - What is it?
  - When to use it?
  - Why use it?
  - How it works?
- ✅ **Working Code** - Minimal but complete example
- ✅ **BPMN Diagram** - Visual process model
- ✅ **Step-by-Step Guide** - Implementation walkthrough
- ✅ **Test Scenarios** - How to test with curl/Postman
- ✅ **Expected Output** - What you should see
- ✅ **Official Docs Links** - Camunda documentation references

---

## 📂 Repository Structure
```
camunda-learning/
├── README.md (this file)
├── camunda-7/
│   ├── 01-tasks-all-types/
│   │   ├── README.md
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/
│   │       │   └── resources/
│   │       │       ├── application.yaml
│   │       │       └── *.bpmn
│   │       └── test/
│   ├── 02-gateways-all-types/
│   ├── 03-events-all-types/
│   ├── ... (15 topics total)
│   └── 15-transaction-subprocess/
├── docs/
│   ├── bpmn-reference.md
│   ├── dmn-reference.md
│   └── camunda-7-vs-8.md
└── common/
    └── diagrams/
```

---

## 🎓 Learning Strategy

### Recommended Order
1. **Start with Tasks** (01) - Understand basic automation
2. **Learn Gateways** (02) - Control flow logic
3. **Master Events** (03) - Event-driven processes
4. **Explore Subprocesses** (04) - Process composition
5. **Add Business Rules** (05) - DMN decisions
6. **Handle Data** (06) - Process variables
7. **Manage Errors** (07) - Robust processes
8. **Integrate Systems** (08-12) - Real-world integration
9. **Advanced Patterns** (13-15) - Complex scenarios

### Time Commitment
- **Minimum**: 2-3 hours per topic
- **Total**: 10 weeks (30-45 hours)
- **Pace**: 1-2 topics per week

---

## 🔗 Official Resources
- [Camunda 7 Documentation](https://docs.camunda.org/manual/latest/)
- [BPMN 2.0 Specification](https://www.omg.org/spec/BPMN/2.0/)
- [DMN 1.3 Specification](https://www.omg.org/spec/DMN/1.3/)
- [Camunda Forum](https://forum.camunda.io/)
- [Camunda Best Practices](https://camunda.com/best-practices/)

---

## ✅ Success Criteria
You've mastered Camunda when you can:
- ✅ Model any business process in BPMN
- ✅ Choose the right task/gateway/event for any scenario
- ✅ Integrate Camunda with external systems (REST, Kafka)
- ✅ Handle errors and exceptions gracefully
- ✅ Deploy and monitor processes in production
- ✅ Write unit tests for processes
- ✅ Optimize process performance

---

**🚀 Ready to start? Go to [01. Tasks - All Types](camunda-7/01-tasks-all-types/)**
