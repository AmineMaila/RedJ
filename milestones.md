# 🧠 Suggested Milestone Roadmap

## Milestone 1 — Core skeleton
* TCP server
* `SET` / `GET`
* In-memory map
> **✅ Checkpoint:** You can already test with `telnet`

## Milestone 2 — Correctness
* Single-threaded execution
* Command abstraction
* Proper error handling
> **✅ Checkpoint:** System becomes predictable

## Milestone 3 — TTL
* `EXPIRE`
* Lazy expiration
* `TTL` command
> **✅ Checkpoint:** Now it feels like Redis

## Milestone 4 — Resource control
* Max keys
* Eviction strategy
> **✅ Checkpoint:** Real-world constraint handling

## Milestone 5 — Polish (optional)
* Logging
* Metrics
* Graceful shutdown