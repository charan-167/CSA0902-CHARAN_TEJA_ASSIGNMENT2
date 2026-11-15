# Task 04: Choosing a Map for a Pharmacy Inventory

**Student:** K V Charan Teja (192424167)  
**Course:** Programming in Java - Assignment 2 (CO2)  
**Bloom's Level:** K4 – Analyse (6 Marks)

---

## 1. Problem Context
A hospital pharmacy manages inventory for thousands of medications identified by unique medicine codes (e.g. `MED-0001042`).
Two operational demands exist:
1. **Intensive Real-Time Lookups:** Pharmacists query medicine stock by code thousands of times each day during dispensing.
2. **Batch Evening Reports:**
   - **Report 1:** Master inventory report sorted strictly by **medicine code** (alphanumeric order).
   - **Report 2:** Stock acquisition audit report in the **exact order medicines were registered** (insertion order).

---

## 2. Theoretical Analysis Before Implementation

| Metric / Map | HashMap | TreeMap | LinkedHashMap |
| :--- | :--- | :--- | :--- |
| **Put Time Complexity** | $O(1)$ amortized | $O(\log n)$ (Red-Black tree rebalancing) | $O(1)$ amortized + link update |
| **Get Time Complexity** | $O(1)$ amortized | $O(\log n)$ (tree comparisons) | $O(1)$ amortized |
| **Iteration Order** | Arbitrary bucket order | **Sorted natural / comparator order** | **Insertion order** (or access order) |
| **Natural Report Fit** | Neither | **Report 1 (Code Order)** | **Report 2 (Insertion Order)** |
| **Penalty for "Wrong" Map** | Requires $O(n \log n)$ sorting + $O(n)$ space for both reports | Requires storing sequence + $O(n \log n)$ sort for Report 2 | Requires $O(n \log n)$ sort for Report 1 |

---

## 3. Empirical Results Across 3 Inventory Sizes

100,000 random lookup queries evaluated across 1,000, 10,000, and 100,000 medicines:

| Inventory Size | Metric / Map | HashMap | TreeMap | LinkedHashMap |
| :--- | :--- | :--- | :--- | :--- |
| **1,000 Medicines**<br>(100k Lookups) | Put Time (ms)<br>100k Lookups (ms)<br>ns / Lookup<br>Report 1 (Code Order)<br>Report 2 (Arrival Order) | 0.244 ms<br>6.600 ms<br>**66.0 ns**<br>0.856 ms (Needs sort)<br>0.645 ms (Needs sort) | 1.677 ms<br>31.994 ms<br>**319.9 ns**<br>**0.106 ms (Natural)**<br>0.615 ms (Needs sort) | 0.434 ms<br>5.670 ms<br>**56.7 ns**<br>1.211 ms (Needs sort)<br>**0.035 ms (Natural)** |
| **10,000 Medicines**<br>(100k Lookups) | Put Time (ms)<br>100k Lookups (ms)<br>ns / Lookup<br>Report 1 (Code Order)<br>Report 2 (Arrival Order) | 1.026 ms<br>8.159 ms<br>**81.6 ns**<br>5.761 ms (Needs sort)<br>4.410 ms (Needs sort) | 4.365 ms<br>28.267 ms<br>**282.7 ns**<br>**1.487 ms (Natural)**<br>4.276 ms (Needs sort) | 0.864 ms<br>7.842 ms<br>**78.4 ns**<br>6.959 ms (Needs sort)<br>**0.202 ms (Natural)** |
| **100,000 Medicines**<br>(100k Lookups) | Put Time (ms)<br>100k Lookups (ms)<br>ns / Lookup<br>Report 1 (Code Order)<br>Report 2 (Arrival Order) | 17.556 ms<br>28.020 ms<br>**280.2 ns**<br>37.788 ms (Needs sort)<br>42.127 ms (Needs sort) | 75.100 ms<br>77.935 ms<br>**779.3 ns**<br>**14.653 ms (Natural)**<br>48.836 ms (Needs sort) | 14.003 ms<br>29.973 ms<br>**299.7 ns**<br>80.334 ms (Needs sort)<br>**1.712 ms (Natural)** |

---

## 4. Scalability Inquiry & Deployment Decision

### Acceptable Response Time Criterion:
- In real-time pharmacy systems, interactive lookups must execute in **$< 1.0 \text{ ms}$**, and ideally **$< 1.0 \text{ microsecond}$ (1,000 ns)** to ensure pharmacists experience zero perceptible interface latency during barcode scanning.
- Both `HashMap` (280 ns) and `LinkedHashMap` (299 ns) comfortably operate well under the 1 µs threshold. `TreeMap` (779 ns) is 2.6x slower due to pointer hopping across Red-Black tree nodes and tree branch mispredictions.

### Final Deployment Recommendation:
- **Recommended Deployment: `LinkedHashMap<String, Medicine>`**.
  - **Reasoning:** Lookups are the primary day-to-day operation (thousands per day) where `LinkedHashMap` provides near-instantaneous $O(1)$ response (299 ns).
  - For evening reports, `LinkedHashMap` emits **Report 2 (Arrival order) instantly in 1.7 ms** with zero sorting overhead.
  - Generating Report 1 requires sorting keys once per evening, which takes only **80 ms** for 100,000 items. Sacrificing 80 ms once at night in batch mode to preserve sub-microsecond interactive response throughout the entire day is the optimal engineering trade-off.

### Why `Hashtable` Is Obsolete:
1. **Coarse-Grained Synchronization:** Every method (`get()`, `put()`, `remove()`) is synchronized on `this`. In a multi-user hospital environment, all pharmacy terminals block on the single lock, causing total thread serialization.
2. **Null Values Prohibited:** Throws `NullPointerException` if a key or value is `null`.
3. **Legacy Enumerations:** Uses old `Enumeration` instead of fail-fast `Iterator`.
4. **Modern Alternatives:** `ConcurrentHashMap` provides lock striping / CAS operations without locking read operations, while `HashMap` provides non-synchronized performance for single-threaded or local operations.

---

## 5. Alternative Concept & Complexity Analysis

### Array Sorted by Medicine Code with `Arrays.binarySearch()`:
- **Lookup:** Binary search achieves $\mathcal{O}(\log n)$ time. With 100,000 medicines, $\lceil \log_2(100000) \rceil \approx 17$ comparisons.
- **Insertion (Stock additions):** $\mathcal{O}(n)$ time. Inserting a new medicine requires shifting an average of $n/2$ references in memory. For 100,000 items, insertion is slow.
- **Code Order Report:** $\mathcal{O}(n)$ sequential iteration. Because elements are already stored in contiguous memory in code order, printing Report 1 requires zero sorting, zero extra memory, and benefits from perfect CPU L1 cache prefetching.
- **Space Efficiency:** Requires exactly one reference slot (8 bytes) per medicine with zero overhead for bucket pointers or tree nodes, consuming significantly less memory than `HashMap` (32 bytes/node) or `TreeMap` (40 bytes/node).

---

## 6. How to Compile and Run
```bash
# Compile
javac Task04/PharmacyInventoryBenchmark.java

# Run
java -cp Task04 PharmacyInventoryBenchmark
```
