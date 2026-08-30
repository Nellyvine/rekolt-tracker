# Change Log of Deviations from design-v1

This log records where the built system differs from the original paper design
(documentation the submitted one), and why each change was made.

## 1. Grade represented as an enum, not a String

**Original plan:** grade was represented as a `String` ("A", "B", "C", "REJECT"), with a
separate static method looking up the multiplier for a given string.

**As built:** `Grade` is an `enum` with four constants (A, B, C, REJECT), each constructed
with its own `multiplier` field, plus a static `fromScore(int)` factory method.

**Reason:** a `String` cannot guarantee only four values ever exist,a typo would compile
without error and only fail (or silently misprice a delivery) at runtime. The enum makes the
four grades a closed, compiler-enforced set, and keeps the multiplier attached to the grade
it belongs to instead of a separate lookup table that could drift out of sync. This also
directly satisfies the requirement for "a Grade enum carrying its own multiplier."

## 2. Absent-search returns `Optional<Delivery>`, not `null`

**Original plan:** `findDeliveryById()` would return `null` when no delivery matched.

**As built:** it returns `Optional<Delivery>`.

**Reason:** returning `null` silently pushes the responsibility for checking onto every
caller; a forgotten check throws a `NullPointerException` far from the actual bug.
`Optional<Delivery>` makes the "might be absent" case part of the method's type signature,
so it cannot be accidentally ignored.

## 3. Input validation and formatting extracted into `util` classes

**Original plan:** validation logic lived as private static methods directly inside `Main`.

**As built:** validation moved into `ConsoleInput` and money formatting into `Formatter`,
both in the `util` package, with `Main` reduced to wiring the menu to these and to the
`service` layer.

**Reason:** once the `model` / `service` / `util` / `app` package split was introduced for
Objective 5, keeping console I/O logic inside `Main` would have left `app` doing work that
belongs in `util`. Separating it also makes `ConsoleInput` independently testable in
principle, without a running `main()` method.

## 4. REJECT filtering uses an explicit `Iterator`, not `List.removeIf()`

**Original plan:** no specific removal mechanism was pinned down at design time beyond
"remove the REJECT deliveries from a working list."

**As built:** `nonRejectedDeliveries()` uses `Iterator.remove()` explicitly, rather than the
shorter, more idiomatic `list.removeIf(d -> d.getGrade() == Grade.REJECT)`.

**Reason:** the assignment specifically asks for removal to be demonstrated through an
`Iterator`, to show understanding of why a `Collection` can only be safely mutated
mid-traversal through its own iterator, an ordinary loop calling `list.remove()` directly
would throw a `ConcurrentModificationException`. `removeIf()` would produce the same result
while hiding that mechanism.

## 5. Word-document generation isolated into its own service and exception type

**Original plan:** Objective 6 was scoped at design time only at the level of "write a
season-report.docx with try-with-resources and specific exception handling."

**As built:** a dedicated `DocumentService` class owns all Apache POI logic, paired with a
custom checked exception, `ReportGenerationException`, thrown with an actionable message and
wrapping the underlying `IOException`.

**Reason:** keeping the POI-specific code out of `Main` and out of `SeasonService` keeps
each class focused on one responsibility, and a custom exception type lets `Main` show the
user a clean, specific message instead of a generic exception or a stack trace.