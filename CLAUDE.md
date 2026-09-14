# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Collection of solutions to frequently-asked Low-Level Design (LLD) interview questions, implemented as standalone
Java classes inside a single Spring Boot skeleton project. Each design (Meeting Scheduler, Snake and Ladder,
Logging System, Tic-Tac-Toe, Null Object pattern, custom HashMap, etc.) lives in its own package under
`src/main/java/com/shubham/app/` and has its own `main`/`MainClass` entry point — the Spring Boot app itself
(`AppApplication`) is not used to run these designs, it's just the build scaffold.

Class diagrams: https://app.diagrams.net/#G1-OR89cucBZWTtqZYyJ29FJIjyd25vxs0

## Common commands

Build (formats code with Spotless, then compiles):
```bash
./mvnw clean install
```

Apply formatting only:
```bash
./mvnw spotless:apply
```

Check formatting without modifying files (runs automatically during `compile` phase, so a plain build will fail on
unformatted code):
```bash
./mvnw spotless:check
```

Run all tests:
```bash
./mvnw test
```

Run a single test class:
```bash
./mvnw test -Dtest=GamePlayServiceTest
```

Run a single test method:
```bash
./mvnw test -Dtest=GamePlayServiceTest#someTestMethod
```

Run one design's `main` method directly (after building), e.g. the logging system demo:
```bash
./mvnw compile exec:java -Dexec.mainClass=com.shubham.app.loggingsystem.MainClass
```
(or run the class's `main` from an IDE — most designs are plain Java demos with no Spring wiring).

`start.sh` is a convenience script that frees port 8080, runs `spotless:apply` + `clean install`, and is set up to
launch the packaged jar (the jar-run line is currently commented out).

## Architecture / conventions

- **One package per design problem** under `com.shubham.app.<designname>`, each self-contained with its own
  `entity`/`dao`/`service`/`dto` sub-packages as needed. There is no shared domain model between designs — don't
  reach across design packages.
- **Entry point per design**: each package has a `Main.java` or `MainClass.java` with a `main` method that wires up
  the objects and demonstrates the pattern (no Spring `@Component`/`@Service` annotations — these are plain
  `new Foo(...)` object graphs, e.g. the logging system chains processors via constructor injection:
  `new TraceLogProcessor(new DebugLogProcessor(new InfoLogProcessor(...)))`).
- **DAO layer**: designs needing persistence use a `Dao`/`DaoImpl` interface pair (e.g. `nullobject.dao.UserDao` /
  `UserDaoImpl`, `tictactoe.dao.BoardDAO` / `BoardDAOImpl`) backed by in-memory data structures, not a real
  database — `AppApplication`'s Spring context and `spring-boot-starter-web` are unused by these designs.
- **Design patterns are the point**: each package exists to demonstrate a specific GoF/LLD pattern (e.g.
  `nullobject` = Null Object pattern, `loggingsystem` = Chain of Responsibility). When extending a design, preserve
  the pattern it's demonstrating rather than optimizing it away.
- **Formatting is enforced at build time** via the Spotless plugin (google-java-format + import ordering +
  4-space indent) bound to the Maven `compile` phase — a build fails if code isn't formatted, so run
  `./mvnw spotless:apply` before committing.
- **Tests** live under `src/test/java/com/shubham/app/<designname>/...` mirroring the main package structure, using
  `spring-boot-starter-test` (JUnit 5).
