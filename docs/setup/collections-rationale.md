# Collections Rationale

Each collection below was chosen against three things: how the data is accessed, whether
ordering matters, and the cost of the operations actually performed on it.

## Arrays: price list source data and the weekly grid

`ProduceCatalog` holds three parallel arrays (`CODES`, `NAMES`, `BASE_PRICES`) as the fixed
source-of-truth for the four produce types, and `SeasonService` holds `weeklyGrid` as a
`double[20][4]`.

Why an array and not a List: both are fixed-size by definition, exactly 4 produce types,
exactly 20 possible weeks, so the size never changes at runtime. An array gives O(1)
index access with no resizing overhead, which a `List` would carry for no benefit here.

Rejected alternative: an `ArrayList<double[]>` for the grid, so each week's row could be
added dynamically. Rejected because the number of weeks (1–20) is a known, fixed business
rule from the spec, not something that grows during the season, a raw 2D array expresses
that fixed shape directly, and it's what the rubric explicitly asks for.

## ArrayList<Delivery>: the season's delivery list

Why: deliveries are added continuously through the season and need to preserve insertion
order for anything display-related (e.g. printing them back in the order recorded).
`ArrayList` gives O(1) amortised append and O(1) index access, and unlike a `LinkedList` it
doesn't pay a pointer-chasing cost when we later iterate it for totals or sorting.

Rejected alternative: `LinkedList<Delivery>`. Rejected because nothing in this
application removes from the middle of the list frequently (the one removal case,
filtering REJECTS,  builds a separate working copy rather than mutating the master list
in place), so `LinkedList`'s O(1) middle-removal advantage is never actually used, while its
worse random-access and cache locality would be a real cost every time the list is iterated.

## HashMap<String, Double>: total payment per member

Why: the report needs to look up "what does member M-0042 owe in total" by ID,
repeatedly, without caring about the order members were first seen. `HashMap` gives O(1)
average lookup and update, which matters because `recordDelivery()` updates this map on
every single delivery recorded — a season of hundreds of deliveries means hundreds of
these updates.

Rejected alternative: `TreeMap<String, Double>`, which would keep members in sorted ID
order automatically. Rejected because nothing in the spec requires the totals to be
displayed in ID order (and even if it did, sorting once at display time is cheaper than
paying `TreeMap`'s O(log n) cost on every single update throughout the season).

## Map<String, List<Delivery>> — deliveries per member

Why: the Word report needs every individual delivery for one member, grouped, to build
that member's table. A map from ID to a list of that member's deliveries is a direct match
for "give me everything belonging to this key," and `computeIfAbsent()` makes the
first-delivery-for-a-new-member case a one-liner rather than a null-check branch.

Rejected alternative: a single flat `List<Delivery>`, filtered by member ID every time
the report needed one member's deliveries. Rejected because that would mean an O(n) scan
of the entire season for every member section written, whereas the map-of-lists pays the
grouping cost once, incrementally, as deliveries come in.

## HashSet<String>: distinct member IDs

Why: the report needs to know how many member sections to generate, and whether an ID
has been seen before, in O(1). A `Set`'s whole contract is "no duplicates, fast membership
check" exactly what's needed here, and nothing about member ID order matters for this use.

Rejected alternative: deriving the distinct count from `totalPaymentPerMember.keySet()`
directly, skipping a separate `Set` entirely, since a `HashMap`'s key set is already
duplicate-free. This was a closer call either is defensible but a dedicated
`HashSet<String>` was kept because the rubric asks for it explicitly as its own collection,
and it also decouples "which members exist" from "what a member is owed," which are
conceptually different questions even though one map's keys currently happen to answer both.

## Sorting: Comparator and Comparable

`Delivery implements Comparable<Delivery>`, ordering naturally by ID
(`sortedById()`), while `topDeliveriesByValue()` uses a `Comparator` built with
`Comparator.comparingDouble(Delivery::calculateNetPayable).reversed()`.

Why two different mechanisms: natural ID ordering is a single, obvious default for a
`Delivery`, which is exactly what `Comparable` is for one canonical ordering per class.
Sorting by value, descending, is a second, situational ordering only needed for the "top 5"
screen; forcing that into `compareTo()` would make ID-ordering impossible to get back
without extra code. A `Comparator` supplied at the call site keeps both orderings available
without the class having to choose one as "correct."

## Iterator: removing REJECT deliveries

`nonRejectedDeliveries()` explicitly uses `Iterator.remove()` rather than the shorter
`list.removeIf(...)`. This was a deliberate choice to demonstrate the underlying mechanism:
a `List` can only be safely mutated during traversal through its own iterator's `remove()`
method — an ordinary for-each loop calling `list.remove()` directly throws a
`ConcurrentModificationException`, since the list detects it was changed by something other
than the iterator that's currently walking it. `removeIf()` hides that mechanism inside the
JDK; the explicit `Iterator` version shows it.

## Optional<Delivery>: search by identifier

`findDeliveryById()` returns `Optional<Delivery>` rather than `null` on a miss.
Rejected alternative: returning `null`. Rejected because it pushes a silent
responsibility onto every caller to remember to null-check, where a forgotten check throws
a `NullPointerException` far from the real bug. `Optional` makes "this might not exist"
part of the method's actual type signature.