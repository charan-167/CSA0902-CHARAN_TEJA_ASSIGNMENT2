# Task 01: Building Text Under Load: String, StringBuilder or StringBuffer?

**Student:** K V Charan Teja (192424167)  
**Course:** Programming in Java - Assignment 2 (CO2)  
**Bloom's Level:** K4 – Analyse (6 Marks)

---

## 1. Problem Context
A railway reservation system generates an e-ticket for every booking by joining numerous fields (PNR, train details, passenger names, coach, berth, fare breakdown, quota, and timestamps). The legacy implementation uses the `+` operator inside a loop. This investigation evaluates whether replacing `String (+)` with `StringBuilder` or `StringBuffer` yields measurable engineering advantages under load.

---

## 2. Reasoned Predictions (Before Implementation)

1. **String (+)**:
   - **Mechanism:** Java strings are immutable (`java.lang.String`). Concatenating with `+` inside a loop requires allocating a new `String` object, allocating a new backing `byte[]` array, and copying all previously concatenated characters plus the new field on each step.
   - **Prediction:** As the number of fields ($n$) grows, character copying scales as $\sum_{i=1}^n i = O(n^2)$. Under high ticket volume ($N$), total memory allocations scale as $O(N \cdot n^2)$ bytes and $O(N \cdot n)$ temporary objects, leading to severe heap bloat, GC pauses, and non-linear degradation.
2. **StringBuilder**:
   - **Mechanism:** Maintains a mutable expandable buffer (`byte[]`). Appending appends characters in-place, doubling buffer capacity only when exceeded (amortized $O(1)$ per append).
   - **Prediction:** Linear execution time $O(n)$ per ticket, minimal object creation (only 1 final string returned per ticket), and dramatically higher throughput.
3. **StringBuffer**:
   - **Mechanism:** Similar to `StringBuilder`, but every mutating method (`append()`, `insert()`) is `synchronized`.
   - **Prediction:** For single-threaded workloads, the JVM's biased locking and escape analysis will eliminate some lock overhead, making it perform almost as fast as `StringBuilder`. However, in highly concurrent multi-threaded scenarios, lock contention would become noticeable.

---

## 3. Workload Design
Three contrasting workloads were designed:
- **Workload 1 (Short Ticket):** 1 Ticket with 10 fields, repeated 1,000 times (measuring minimal single-ticket latency).
- **Workload 2 (Standard Bulk):** 10,000 Tickets with 50 fields each (evaluating standard enterprise batch processing).
- **Workload 3 (Custom Enterprise Ticket):** 5,000 Tickets with 100 fields each (evaluating deep quadratic behavior as field count doubles).

---

## 4. Experimental Evidence & Measurements

| Workload | Metric / Implementation | String (+) | StringBuilder | StringBuffer | String.join() |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Workload 1**<br>(1 Ticket x 10 fields, 1000 runs) | Avg Time (ms)<br>Speedup<br>Intermediate Objects | 0.010 ms<br>1.00x (Baseline)<br>10 objects | 0.008 ms<br>**1.23x**<br>1 object | 0.006 ms<br>**1.59x**<br>1 object | 0.004 ms<br>**2.38x**<br>1 object |
| **Workload 2**<br>(10,000 Tickets x 50 fields) | Avg Time (ms)<br>Speedup<br>Intermediate Objects<br>Heap Allocation | 45.385 ms<br>1.00x (Baseline)<br>500,000 objects<br>62.80 MB | 8.385 ms<br>**5.41x**<br>10,000 objects<br>59.00 MB | 8.297 ms<br>**5.47x**<br>10,000 objects<br>59.00 MB | 12.329 ms<br>**3.68x**<br>10,000 objects<br>N/A |
| **Workload 3**<br>(5,000 Tickets x 100 fields) | Avg Time (ms)<br>Speedup<br>Intermediate Objects<br>Heap Allocation | 77.371 ms<br>1.00x (Baseline)<br>500,000 objects<br>89.20 MB | 8.549 ms<br>**9.05x**<br>5,000 objects<br>59.00 MB | 8.228 ms<br>**9.40x**<br>5,000 objects<br>59.00 MB | 6.377 ms<br>**12.13x**<br>5,000 objects<br>N/A |

### Comparison of Predicted vs Observed Behavior:
- **Observed:** In Workload 3 (100 fields), `String (+)` took **77.37 ms** while `StringBuilder` took only **8.55 ms** (a **9.05x speedup**).
- As the field count per ticket doubled from 50 to 100, `String (+)` execution time grew from 45.38 ms to 77.37 ms despite running on half the ticket volume ($5,000$ vs $10,000$), confirming the $O(n^2)$ quadratic degradation per ticket.
- `StringBuilder` allocated only final ticket strings (5,000 objects) compared to 500,000 intermediate objects for `String (+)`, saving massive heap churning and GC pressure.

---

## 5. Design Decision for Multithreaded Booking Server

In an enterprise multithreaded railway server:
1. **Per-Ticket Text Generation:**
   - **Recommendation:** `StringBuilder`.
   - **Defense:** Each ticket is generated within a single request worker thread (thread-confined on the stack). Because the instance is never shared across threads, thread synchronization is completely redundant. `StringBuilder` eliminates lock acquisition instructions and guarantees maximum throughput.
2. **Shared Booking Log:**
   - **Recommendation:** `StringBuffer` (or an asynchronous concurrent queue / SLF4J / Log4j buffer).
   - **Defense:** Multiple concurrent worker threads append audit messages to the same shared log destination. Using `StringBuilder` here causes race conditions: corrupted character arrays, overwritten indices, and `ArrayIndexOutOfBoundsException`. `StringBuffer`'s synchronized methods guarantee thread-safe serialised appends.

---

## 6. Alternative Concept & Complexity Analysis

### Time and Space Complexity Derivation:
Let $n$ = number of fields, $L$ = average length of each field string:
- **String (+):**
  - Iteration 1: copies $L$ chars.
  - Iteration 2: copies $2L$ chars.
  - Iteration $i$: copies $i \cdot L$ chars.
  - Total character copies: $\sum_{i=1}^n i \cdot L = L \cdot \frac{n(n+1)}{2} = \mathcal{O}(n^2)$.
  - Auxiliary Space: $\mathcal{O}(n^2)$ total bytes allocated in temporary strings.
- **StringBuilder:**
  - Amortized append cost: $\mathcal{O}(1)$ per character.
  - Total copies across $n$ appends: $\mathcal{O}(n \cdot L) = \mathcal{O}(n)$.
  - Auxiliary Space: $\mathcal{O}(n)$ bytes in a single reusable buffer.

### Comparison with Alternatives:
- `String.join(delimiter, list)` / `StringJoiner`:
  - Implemented internally using pre-sized byte arrays (`java.lang.StringJoiner`). Achieves linear $\mathcal{O}(n)$ time and performs exceptionally well when all elements are known in advance in a collection.
- `String.format("%s...", ...)`:
  - Requires runtime parsing of format specifier strings, regular expression validation, and primitive auto-boxing. Measurements show it is 5x to 10x slower than `StringBuilder`, making it unsuitable for high-throughput batch generation.

---

## 7. How to Compile and Run
```bash
# Compile
javac Task01/TicketBenchmark.java

# Run and inspect results
java -cp Task01 TicketBenchmark
```
