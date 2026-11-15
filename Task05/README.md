# Task 05: Derive a Generic Ranking Component from a Principle

**Student:** K V Charan Teja (192424167)  
**Course:** Programming in Java - Assignment 2 (CO2)  
**Bloom's Level:** K4 – Analyse (7 Marks)

---

## 1. Problem Context
An inter-college sports meet ranks teams by:
1. **Points** (higher first)
2. **Goal Difference** (higher first, tie-breaker 1)
3. **Team Name** (alphabetical A–Z, tie-breaker 2)

The fundamental requirement is to derive a generic, reusable `Leaderboard<T>` class from first principles:
> *"A ranking is correct only if the comparison used is consistent: if A ranks above B and B ranks above C, then A must rank above C, and equal items must compare as equal."*

---

## 2. Complete the Code Execution & Verification

### Completed `LeaderboardDemo.java`:
- **TODO 1 (Defensive Copy & Sorting):**
  ```java
  List<T> ranking() {
      List<T> sortedCopy = new ArrayList<>(items);
      sortedCopy.sort(order);
      return sortedCopy;
  }
  ```
- **TODO 2 (Chained Comparator Construction):**
  ```java
  Comparator<Team> byRank = Comparator
          .comparingInt((Team t) -> t.points).reversed()
          .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
          .thenComparing((Team t) -> t.name);
  ```

### Program Output:
```text
[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
```

### Why Bisons is Ranked Above Panthers:
1. **Points Check:** Eagles (9), Bisons (9), Panthers (9), Cobras (6). Cobras is placed 4th.
2. **Goal Difference Check:** Among the 9-point teams, Eagles has `goalDiff = 6` (Rank 1). Bisons and Panthers both have `goalDiff = 4` (Tied).
3. **Name Check:** Because points and goal difference are equal, the comparator cascades to `thenComparing((Team t) -> t.name)`.
4. Comparing names lexicographically: `"Bisons".compareTo("Panthers") < 0` (negative integer, since 'B' precedes 'P').
5. Therefore, **Bisons is ranked above Panthers** ($B < P$).

---

## 3. Engineering Design Decisions

### A. Should `Team` implement `Comparable<Team>` or use separate `Comparator`s?
- **Decision:** Separate `Comparator` objects are vastly superior.
- **Justification:**
  1. **Single Responsibility & Open/Closed Principle:** A data class should represent the state of an entity. Embedding a specific sorting rule hardcodes one ranking policy. If another tournament ranks by head-to-head record or fair-play points, modifying `Team` breaks existing code.
  2. **Multi-Domain Reusability:** By decoupling comparison into an external strategy (`Comparator<? super T>`), the exact same `Leaderboard<T>` class ranks `Team`, `Player`, `College`, or `QuizTeam` without changing a single line of code.

### B. Bounded Wildcards: `Comparator<? super T>`
- **Role:** PECS Principle ("Producer Extends, Consumer Super").
- The leaderboard is consuming objects of type `T` and passing them to the comparator.
- Declaring `Comparator<? super T>` allows a comparator defined on a superclass (e.g. `Competitor` or `NamedEntity`) to rank subclasses of `T` (e.g. `Team extends Competitor`), maximizing polymorphism and flexibility.

---

## 4. Correctness Inquiry & Principle Verification

The comparison obeys the mathematical properties of a **Strict Weak Ordering**:
1. **Irreflexivity:** For any team $A$, `compare(A, A) == 0`.
2. **Antisymmetry:** If `compare(A, B) < 0`, then `compare(B, A) > 0`.
3. **Transitivity of Superiority:** If `compare(A, B) < 0` and `compare(B, C) < 0`, then `compare(A, C) < 0`.
4. **Transitivity of Equivalence:** If `compare(A, B) == 0` and `compare(B, C) == 0`, then `compare(A, C) == 0`.

### Breaking Faulty Comparators (Empirical Verification):
- **Faulty Subtraction Comparator:** `(t1, t2) -> t2.points - t1.points`.
  - When comparing a team with $+2,000,000,000$ points and a team with $-2,000,000,000$ points, $(-2 \cdot 10^9) - (2 \cdot 10^9) = -4 \cdot 10^9$. In 32-bit signed integers, this underflows to $+294,967,296$.
  - The subtraction incorrectly concludes that the negative-point team ranks above the positive-point team!
  - `Integer.compare(a, b)` avoids subtraction and compares via relational operators (`<`, `>`), ensuring 100% mathematical safety.

---

## 5. Alternative Concept & Complexity Analysis

### Comparison Strategy vs Three Stable Sorts:

| Approach | Time Complexity | Extra Space | Code Maintainability |
| :--- | :--- | :--- | :--- |
| **Chained Comparator (`List.sort`)** | $\mathcal{O}(n \log n)$ (Single pass TimSort) | $\mathcal{O}(n)$ (Defensive copy) | Clean, declarative, self-documenting |
| **Three Stable Sorts (Radix-like)** | $3 \times \mathcal{O}(n \log n)$ | $\mathcal{O}(n)$ | Fragile; must be applied in reverse order (Name $\to$ GD $\to$ Points) |
| **`Comparable<Team>` Interface** | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n)$ | Inflexible; tightly couples `Team` to one ranking policy |

---

## 6. How to Compile and Run
```bash
# Compile
javac Task05/LeaderboardDemo.java Task05/LeaderboardTest.java

# Run demo
java -cp Task05 LeaderboardDemo

# Run extended tests
java -cp Task05 LeaderboardTest
```
