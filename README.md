# 🧠 mini-redis

> A Redis-inspired server implemented in **Java 17** — built to **learn internals**, not to replace Redis 🚀

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-blue" />
  <img src="https://img.shields.io/badge/status-stable%20for%20implemented%20commands-yellowgreen" />
  <img src="https://img.shields.io/badge/focus-learning%20project-informational" />
</p>

---

## 📌 Overview

**mini-redis** is a compact, Redis-compatible server written in Java.  
It was built as a **learning project** to explore:

- Redis internals
- RESP protocol parsing
- Concurrency and command execution models
- Java systems programming

The emphasis is on **clarity, correctness, and architecture**, not production completeness.

> ⚠️ This project is **not** a production Redis replacement.

---

## 🎯 Project Goals

- ✅ Understand Redis command execution semantics
- ✅ Implement core Redis data types and commands
- ✅ Explore concurrency models (Actor-style execution)
- ❌ Persistence, clustering, and replication (out of scope for now)

---

## ⚙️ Implemented Commands

Commands currently implemented in `src/commands`:

### 🔑 Key Commands
- `DEL`
- `EXPIRE`
- `TTL`
- `EXISTS`

### 🔤 String Commands
- `GET`, `SET`, `MSET`
- `APPEND`
- `INCR`, `INCRBY`
- `DECR`, `DECRBY`
- `STRLEN`

### 📚 List Commands
- `LPUSH`, `RPUSH`
- `LRANGE`
- `LLEN`
- `LINDEX`
- `LPOP`, `RPOP`
- `LSET`

### #️⃣ Hash Commands
- `HSET`, `HGET`
- `HMGET`, `HGETALL`
- `HDEL`
- `HKEYS`, `HVALS`
- `HLEN`
- `HINCRBY`
- `HEXISTS`

### 📙 Set Commands
- `SADD`, `SREM`
- `SCARD`
- `SMEMBERS`

> 🟢 All listed commands are stable and tested within the current architecture.

---

## 🧩 Architecture Overview

mini-redis is intentionally split into **clear, inspectable modules**, making it easy to extend and reason about.

---

## 🧵 Threading & Execution Model

The server uses a **hybrid concurrency model**:

### Client Side
- Each client connection runs on its **own thread**
- Responsibilities:
  - Blocking socket I/O
  - RESP parsing
  - Command construction
  - Enqueuing work

### Execution Side
- A **single dispatcher thread** (Actor-style)
- Ensures:
  - Atomic command execution
  - Deterministic behavior
  - Minimal locking in the data store

### Why this model?
> Parsing and I/O remain concurrent, while **all command execution is serialized**, closely matching Redis’ execution guarantees.

---

## 🔁 Execution Flow (Diagram)

```mermaid
flowchart LR
    subgraph Clients["Client Threads"]
        C1["Client #1"]
        C2["Client #2"]
        C3["Client #3"]
    end

    Q["LinkedBlockingQueue<WorkItem>"]

    subgraph Dispatcher["Command Dispatcher"]
        D["Poll → Execute Command (Atomic)"]
    end

    C1 --> Q
    C2 --> Q
    C3 --> Q
    Q --> D
