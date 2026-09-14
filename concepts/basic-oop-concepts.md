# Basic OOP Concepts

The four pillars — asked in almost every LLD round as a warm-up, and implicitly tested throughout the rest of the
interview via the class design itself. Being able to *name* them isn't the bar; being able to point at a design
decision and say "this is encapsulation because..." is.

## 1. Encapsulation

Bundling data with the methods that operate on it, and hiding internal state behind a controlled interface —
callers interact through methods, never by reaching into fields directly.

**Why it's asked:** it's the difference between a class that can enforce its own invariants and a plain data bag
that any caller can corrupt.

**Example — why a public field is worse:**

```java
// Bad: any caller can set an invalid balance, skip validation, break the invariant
class Account {
    public double balance;
}
account.balance = -500; // no way to prevent this

// Good: the class owns its invariant
class Account {
    private double balance;

    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalArgumentException("insufficient funds");
        balance -= amount;
    }

    public double getBalance() { return balance; }
}
```

In this repo: `hashmapimp.CustomHashMap` keeps its backing array/size private and only exposes `put`/`get`/`remove` —
callers can't reach in and desync the size counter from the actual bucket contents.

**Interview tip:** if you find yourself exposing a raw mutable collection or field just so another class can "poke"
at state, that's usually a sign a method belongs on the class that owns the data, not on the caller.

**Common follow-ups:**

- *"Isn't a getter/setter pair just as bad as a public field?"* — Not quite: a getter/setter still routes through a
  method, so you can add validation, logging, or lazy computation later without breaking callers. A public field
  can never grow that seam. That said, a getter/setter pair with no logic in either is a smell worth naming — it
  usually means the behavior belongs on the class holding the field, not on whoever's calling the setter.
- *"What's the difference between encapsulation and abstraction, then?"* — Encapsulation is the *mechanism*
  (hiding state behind methods); abstraction is the *design intent* (hiding implementation behind a contract).
  Encapsulation is how a single class protects its own data; abstraction is how a caller is shielded from knowing
  which concrete class it's even talking to (see §4).

## 2. Inheritance

An `is-a` relationship where a subclass acquires the fields/methods of a superclass (or implements an interface's
contract) and can extend or override behavior.

**Why it's asked:** interviewers want to see whether you reach for it correctly — for genuine "is-a" hierarchies —
and whether you know when *not* to reach for it.

**When it's right:** `tictactoe.entity.Symbol` / `snakeladder` piece hierarchies — a `Snake` and a `Ladder` really
are both `is-a` board-entity with a jump effect; sharing behavior through a common supertype is natural.

**When composition is better:** favor composition when you want to *change behavior at runtime* or when the
relationship is "has-a"/"uses-a" rather than "is-a". Example: `loggingsystem` doesn't make `DebugLogProcessor`
extend `InfoLogProcessor` — each processor *holds a reference to* the next one in the chain (composition), so the
chain's order and membership can be reconfigured without touching the class hierarchy at all.

**Example — inheritance (is-a) vs. composition (has-a):**

```java
// Inheritance: genuine is-a — a Snake IS-A board entity, sharing the jump-effect contract
abstract class BoardEntity {
    abstract int jumpTo(int currentPosition);
}

class Snake extends BoardEntity {
    private final int head, tail;
    Snake(int head, int tail) { this.head = head; this.tail = tail; }
    int jumpTo(int currentPosition) { return currentPosition == head ? tail : currentPosition; }
}

// Composition: LogProcessor HAS-A next processor — chain is reconfigurable at construction time,
// with no shared base class between processors beyond the single-method interface
class DebugLogProcessor implements LogProcessor {
    private final LogProcessor next; // composition, not inheritance
    DebugLogProcessor(LogProcessor next) { this.next = next; }

    public void write(LoggingLevel level, String message) {
        if (level == LoggingLevel.DEBUG) System.out.println("DEBUG: " + message);
        else if (next != null) next.write(level, message); // delegate, don't override a shared parent
    }
}
```

**Interview tip:** the classic tell for "inheritance was the wrong choice" is needing to override a method to throw
`UnsupportedOperationException`, or a deep hierarchy where a change to a base class ripples unpredictably through
subclasses. If asked "would you use inheritance or composition here?", default answer: composition, unless there's
a genuine is-a relationship with substitutable behavior (see Polymorphism below).

**Common follow-ups:**

- *"What's the Liskov Substitution Principle, and how does it relate here?"* — LSP says a subclass must be usable
  anywhere its supertype is expected, without the caller noticing a difference in behavior. The classic violation:
  `Square extends Rectangle` and overrides `setWidth`/`setHeight` to keep both sides equal — code that works with
  a `Rectangle` and calls `setWidth` then checks the resulting area breaks when handed a `Square`. If your
  hierarchy needs a subclass to narrow preconditions or weaken postconditions to "fit," that's the same tell as
  the `UnsupportedOperationException` smell above — the is-a relationship isn't real.
- *"Multiple inheritance — why doesn't Java allow it for classes?"* — the diamond problem: if two parent classes
  both define a method, the compiler can't unambiguously pick which implementation the child inherits. Java sidesteps
  this by allowing only single class inheritance, but permits implementing *multiple interfaces* (interfaces didn't
  carry field/implementation state before Java 8's default methods, and even now the rules for resolving conflicting
  default methods are explicit rather than implicit).

## 3. Polymorphism

The same operation behaves differently depending on the object it's invoked on.

- **Compile-time (overloading):** multiple methods with the same name, different parameter lists, resolved at
  compile time.
- **Runtime (overriding):** a subclass/implementation provides its own version of a method declared in a
  supertype/interface; the JVM dispatches to the correct one based on the actual object type at runtime.

**Why it's asked:** runtime polymorphism is *the* mechanism that makes Strategy, State, and most other GoF patterns
work — an interviewer watching whether your Strategy/State example actually swaps implementations polymorphically
(rather than via an `if/else` on a type enum) is testing this concept indirectly.

**Example tied to a pattern:** in `loggingsystem`, `LogProcessor.write(...)` is declared once on the interface;
`DebugLogProcessor`, `InfoLogProcessor`, `WarnLogProcessor`, etc. each override it. The chain in `MainClass` calls
`write` on whatever concrete processor it's holding without ever checking `instanceof` — that's runtime
polymorphism doing the dispatch.

**Example — overloading (compile-time) vs. overriding (runtime):**

```java
class Printer {
    // Overloading: same name, different parameter list — the compiler picks the method at compile time
    void print(String s) { System.out.println(s); }
    void print(int i) { System.out.println(i); }
}

interface Shape {
    double area();
}

class Circle implements Shape {
    private final double radius;
    Circle(double radius) { this.radius = radius; }
    public double area() { return Math.PI * radius * radius; } // overriding
}

class Rectangle implements Shape {
    private final double w, h;
    Rectangle(double w, double h) { this.w = w; this.h = h; }
    public double area() { return w * h; } // overriding
}

// Runtime polymorphism: the declared type is Shape, but area() resolves to whichever
// concrete class the object actually is — decided at runtime, not by the variable's type
for (Shape shape : List.of(new Circle(2), new Rectangle(3, 4))) {
    System.out.println(shape.area()); // no instanceof/switch needed
}
```

**Interview tip:** if your design has a switch/if-else chain branching on a "type" field to decide behavior, that's
usually a missed opportunity for polymorphism — replace the branch with an overridden method on a small type
hierarchy or a Strategy implementation.

**Common follow-ups:**

- *"How does the JVM actually pick which overridden method to run?"* — via **dynamic dispatch**: every object
  carries a reference to its class's method table, and the call is resolved against the *runtime* type of the
  object, not the *declared* (compile-time) type of the variable. That's why `LogProcessor lp = new DebugLogProcessor(...)`
  calling `lp.write(...)` still runs `DebugLogProcessor`'s override, even though the variable is declared as the
  interface type.
- *"Is overloading really polymorphism?"* — it's usually filed under polymorphism (specifically *static*/
  compile-time polymorphism) because the same method name behaves differently per call site, but it's resolved by
  the compiler based on argument types, not by runtime dispatch — worth naming the distinction if asked to be
  precise, since it's the *runtime* form that patterns like Strategy/State actually depend on.

## 4. Abstraction

Exposing only the essential behavior of a component and hiding the implementation details behind an interface or
abstract class — callers depend on *what* something does, not *how*.

**Interface vs. abstract class — when to reach for which:**

| | Interface | Abstract class |
|---|---|---|
| Use when | Unrelated classes share a *capability/contract* (e.g. `Comparable`, `LogProcessor`) | Related classes share *both* a contract *and* some common state/implementation |
| Multiple inheritance | A class can implement many | A class can extend only one |
| State | No instance fields (pre–Java 8 mindset; still the convention) | Can hold shared fields/constructor logic |
| Example in this repo | `nullobject.entity.UserInterface` — just a contract (`getName()`), implemented by both `User` and `UserNull` | N/A currently — would fit if two piece types shared partial default behavior beyond just a contract |

**Example — the caller depends on the contract, not the concrete class:**

```java
interface UserInterface {
    String getName();
}

class User implements UserInterface {
    private final String name;
    User(String name) { this.name = name; }
    public String getName() { return name; }
}

class UserDaoImpl implements UserDao {
    public UserInterface getUser(int id) {
        // returns either a real User or a UserNull — caller never needs to know which
        return found ? new User(name) : new UserNull();
    }
}

// MainClass only ever depends on the abstraction:
UserInterface user = userDao.getUser(12);
logger.info("user : {}", user.getName()); // works identically for User or UserNull
```

**Why it's asked:** abstraction is what lets a caller (e.g. `MainClass`) depend on `UserDao`/`UserInterface` rather
than concrete classes — this is also *Dependency Inversion* (the "D" in SOLID) and is the seam that makes a design
testable and extensible without modifying existing callers.

**Interview tip:** when you introduce an interface, be ready to say *who benefits* — usually "the caller no longer
needs to know which concrete implementation it's talking to," which is the actual payoff, not just "it's good
practice."

**Common follow-ups:**

- *"Isn't abstraction the same thing as an interface?"* — an interface is one *tool* for achieving abstraction, not
  abstraction itself. You can abstract with an abstract class, or even with a well-designed concrete class that
  simply exposes a small, intention-revealing public API while keeping its fields and helper methods private —
  abstraction is the *design goal* (hide the how, expose the what); interfaces/abstract classes are two of the
  mechanisms Java gives you to enforce it at compile time.
- *"Give an example of abstraction going wrong."* — a "leaky abstraction": an interface method whose Javadoc has to
  explain an implementation detail for callers to use it correctly (e.g. "call `close()` or the underlying file
  handle leaks"). If the caller needs to know *how* something is implemented to use it *correctly*, the abstraction
  boundary is in the wrong place.

## One-paragraph summary

Encapsulation protects a class's invariants by hiding state behind methods; inheritance models genuine is-a
relationships but composition is the safer default for is-uses-a/has-a; polymorphism is the runtime dispatch
mechanism that makes design patterns swap behavior without branching on type; abstraction hides implementation
behind a contract so callers depend on behavior, not concrete classes. In an LLD interview, don't just define
these — point at the specific line of your design where each one is doing work.
