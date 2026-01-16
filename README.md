# mini-redis

mini-redis is a lightweight Redis-compatible server implemented in Java. It began as a personal project to learn Redis internals and practice Java systems programming; it is a small, focused reimplementation of Redis primitives in Java and is not intended as a production replacement for Redis.

Status
- Purpose: Implementing Redis in Java as a learning project (author-centric)
- Stability: Stable for the implemented commands listed below
- Maturity: Work-in-progress — more commands and features are planned

Implemented commands
The commands currently implemented in src/commands (as of the latest commit) include:

- String commands
  - GET, SET, MSET, APPEND, INCR, INCRBY, DECR, DECRBY, STRLEN
- Key commands
  - DEL, EXPIRE, TTL, EXISTS
- List commands
  - LPUSH, RPUSH, LRANGE, LLEN, LINDEX, LPOP, RPOP, LSET

Architecture overview
mini-redis is organized into modular components that separate networking, parsing, command execution, and storage.

- Thread-per-client I/O
  - Each client connection is handled by its own thread. Client threads perform blocking I/O and parse the incoming RESP data stream.

- Actor module / CommandDispatcher (single dispatcher thread)
  - Command execution is serialized through an Actor-style dispatcher: client threads enqueue WorkItem objects into a shared LinkedBlockingQueue and a single CommandDispatcher runnable consumes the queue, polls one command at a time, and executes it against the DataStore.
  - This design gives atomic, sequential command execution inside the dispatcher and simplifies concurrency reasoning for core operations.

- Command parsing
  - A RESP2 parser decodes and encodes requests and responses. The CommandParser converts parsed RESP arrays into concrete Command objects.

- Storage
  - An in-memory DataStore holds keys and typed values (StringValue, ListValue, HashValue, SetValue). Entry objects wrap values stored in the DataStore.

- Concurrency model
  - Per-client threads handle network I/O and parsing of the RESP stream.
  - A single dispatcher thread (the Actor module) processes WorkItem commands sequentially to ensure atomic access and reduce the need for fine-grained locking in the DataStore.

Design notes
- The code structure isolates parsing, storage, and networking to make it easier to add new commands or swap implementations (for example, swap in a persistent store later).
- Emphasis on correctness for core commands before optimizing for extreme performance.

Requirements
- Java 17+

Contributing
- Pull requests and issues are welcome. Contributions that improve correctness, tests, or documentation are appreciated.

License
- Add your preferred license (e.g., MIT) to the repository.

Notes
- This project was created to learn Redis internals and Java server programming. It is not a production Redis replacement and remains a work-in-progress.