# concepts/

Reference material for LLD/OOD interview prep — read once, then drill against an actual mock in
[`practice/`](../practice). Nothing here is specific to one design; each file is the generic knowledge a design
problem ends up testing.

- [`answer-framework.md`](answer-framework.md) — the step-by-step checklist (①–⑧) to follow top to bottom in any
  LLD/OOD interview; every `practice/session-NN-*.md`'s "Ideal design" section follows these same numbered steps.
- [`basic-oop-concepts.md`](basic-oop-concepts.md) — the four pillars of OOP, with a design decision pointed at
  for each one rather than just a definition.
- [`design-principles.md`](design-principles.md) — SOLID, DRY, KISS, YAGNI, each with a before/after example —
  the *justifications* you reach for after drawing a class diagram.
- [`uml-diagrams.md`](uml-diagrams.md) — how to actually draw a class/sequence/use-case diagram live, with a
  worked example backed by [`diagrams/`](diagrams).
- [`concurrency-primitives.md`](concurrency-primitives.md) — `synchronized`, `ReentrantLock`, `Semaphore`,
  `volatile`, and the Atomic types: what each one actually guarantees and when to reach for it over the others.
  Runnable demos for each live under
  [`src/main/java/com/shubham/app/concurrencyprimitives/`](../src/main/java/com/shubham/app/concurrencyprimitives).
- [`diagrams/`](diagrams) — the Mermaid sources + rendered PNGs that back `uml-diagrams.md`'s worked example
  (class, sequence, and use-case diagrams).
