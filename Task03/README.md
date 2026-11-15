# Task 03: Engineer the hashCode–equals Contract

**Student:** K V Charan Teja (192424167)  
**Course:** Programming in Java - Assignment 2 (CO2)  
**Bloom's Level:** K4 – Analyse (6 Marks)

---

## 1. Problem Context
A college fest records attendance by scanning student ID cards across multiple entrance gates. The same student may be scanned at multiple gates. Organizers store scans in a `HashSet<Student>` to keep only unique students. 

A junior developer implemented the `Student` class by overriding `equals()` to compare `regNo`, but **failed to override `hashCode()`**. As a result, duplicate entries appeared in the attendance roster.

---

## 2. Complete the Code: Execution Results

### A. Output BEFORE Completing TODO 1 (Missing hashCode)
```text
Output BEFORE TODO 1: Unique students = 3
Set elements: [192311002 Divya, 192311001 Arun, 192311001 Arun]
```
**Explanation:** 
Two separate `new Student("192311001", "Arun")` instances were created. Because `hashCode()` was not overridden, they inherited `java.lang.Object.hashCode()`, which computes a hash based on the memory address (identity hash). Because their memory addresses differed, `HashSet` assigned them to **different hash buckets**. Because they landed in different buckets, Java never invoked `equals()`, allowing duplicate records.

### B. Output AFTER Completing TODO 1 (Consistent hashCode)
```java
// Completed in Attendance.java:
@Override
public int hashCode() {
    return Objects.hashCode(regNo);
}
```
**Execution Output:**
```text
Unique students: 2
```
**Explanation:**
Now, both `Student` instances with `regNo = "192311001"` produce the exact same `hashCode()`. `HashSet` routes both to the exact same hash bucket. Upon encountering an existing node in that bucket, it evaluates `key.equals(existingKey)`, which returns `true`. The duplicate entry is correctly discarded.

---

## 3. Deliberately Designed Experimental Inquiries

### Test 1: Where HashSet Appears to Work Correctly
- **Design:** Scanning the exact same instance reference twice (`s1` and `s1`).
- **Predicted Size:** 1
- **Reason:** In `HashMap.putVal()`, Java checks `if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))`. When `p.key == key` (same reference), reference identity is true, bypassing the hash comparison entirely.
- **Observed Size:** 1 (Prediction Confirmed).

### Test 2: Deliberately Designed to Expose Duplicate Entries
- **Design:** Two distinct object allocations with identical `regNo` (`new Student("192311001", "Arun")` twice).
- **Predicted Size:** 2 (Duplicates allowed).
- **Reason:** Two separate heap allocations have different default `System.identityHashCode()` values. They map to different buckets, and `equals()` is never called across buckets.
- **Observed Size:** 2 (Prediction Confirmed).

### Test 3A: Mutating a Field Used in hashCode() After Insertion
- **Design:** Insert `s3` with `regNo = "192311001"`. Then mutate `s3.regNo = "192311999"`.
- **Predicted Result:** `set.contains(s3)` returns `false`, and re-adding `s3` produces duplicate ghost entries.
- **Reason:** `s3` was indexed into bucket $B_1$ based on the hash of `"192311001"`. When `regNo` changes to `"192311999"`, `contains()` looks in bucket $B_2$. Bucket $B_2$ is empty. `s3` is permanently trapped in $B_1$ as an unreachable ghost element, breaking set invariants and leaking memory.
- **Observed Result:** `set.contains(s3) = false`, Set size after re-adding = 2 (Prediction Confirmed).

### Test 3B: TreeSet with Comparator Inconsistent with equals()
- **Design:** `TreeSet` with a Comparator comparing only `name`. Insert Divya-1 (`192311002`) and Divya-2 (`192311099`).
- **Predicted Result:** Set size = 1 (Silent data loss).
- **Reason:** `TreeSet` uses `compare(a, b) == 0` exclusively to test for duplicates, ignoring `equals()`. Since both have name "Divya", `compare()` returns 0 and Divya-2 is silently discarded.
- **Observed Result:** Set size = 1 (Prediction Confirmed).

---

## 4. Fundamental Engineering Insights

### How HashSet Locates an Element:
1. Computes hash: $h = \text{key.hashCode()} \oplus (h \gg 16)$.
2. Identifies bucket index: $\text{index} = (n - 1) \ \& \ h$.
3. Searches bucket chain/tree:
   - Compares cached hash values: `p.hash == hash`.
   - If true, tests reference equality: `p.key == key`.
   - If false, tests logical equality: `key.equals(p.key)`.
4. If either test succeeds, the key already exists and duplicate is rejected.

### The General Contract:
1. **Consistency:** If `x.equals(y) == true`, then `x.hashCode() == y.hashCode()` **MUST ALWAYS** be true.
2. **Reverse is NOT required:** If `x.hashCode() == y.hashCode()`, `x.equals(y)` does not need to be true (hash collision).
3. **Immutability:** Fields participating in `hashCode()` and `equals()` must be `final` and immutable.

---

## 5. Alternative Concept & Complexity Analysis

| Approach | Time per Scan | Total Space ($n$ unique) | Ordering | Key Contract Requirement |
| :--- | :--- | :--- | :--- | :--- |
| **HashSet\<Student\>** | $O(1)$ amortized | $O(n)$ (Node table + buckets) | Unordered | Consistent `hashCode()` and `equals()` |
| **TreeSet\<Student\>** | $O(\log n)$ (Red-Black Tree) | $O(n)$ (3 pointers per node: parent, left, right) | Sorted by `regNo` | `Comparator` or `Comparable` consistent with `equals()` |
| **HashMap\<String, Student\>** | $O(1)$ amortized | $O(n)$ | Unordered | Key is immutable `String` (guaranteed safe hash) |

- **Recommendation:** `HashSet<Student>` with immutable `regNo` provides optimal $O(1)$ attendance scanning throughput. If key extraction is preferred, `HashMap<String, Student>` keyed by `regNo` is bulletproof because `java.lang.String` is inherently immutable with a cached, deterministic hash code.

---

## 6. How to Compile and Run
```bash
# Compile
javac Task03/AttendanceBeforeFix.java Task03/Attendance.java Task03/HashCodeContractInvestigation.java

# Run
java -cp Task03 AttendanceBeforeFix
java -cp Task03 Attendance
java -cp Task03 HashCodeContractInvestigation
```
