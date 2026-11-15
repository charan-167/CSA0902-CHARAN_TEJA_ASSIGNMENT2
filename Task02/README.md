# Task 02: When Is ArrayList an Appropriate Engineering Choice?

**Student:** K V Charan Teja (192424167)  
**Course:** Programming in Java - Assignment 2 (CO2)  
**Bloom's Level:** K4 – Analyse (4 Marks)

---

## 1. Problem Context
A music-streaming app lets users build playlists of `Song` objects. Core operations include:
- Appending songs at the end of the playlist
- Inserting songs at the front or arbitrary positions (e.g. inserting as the next track)
- Removing songs
- Jumping directly to the $k$-th song (random access playback)
- Sequentially iterating through the playlist

The initial system stored playlists in `ArrayList<Song>`. This investigation analyzes whether `ArrayList` is an appropriate engineering choice and when it should be replaced.

---

## 2. Experimental Design
Comparative microbenchmarks were designed across three playlist sizes:
- **Small:** 1,000 songs
- **Medium:** 10,000 songs
- **Large:** 50,000 songs

The following operations were measured using high-precision hardware timers (`System.nanoTime()`):
1. Add at End (`add(e)`)
2. Insert at Front (`add(0, e)`)
3. Insert in Middle (`add(size / 2, e)`)
4. Remove from Front (`remove(0)`)
5. Remove from Middle (`remove(size / 2)`)
6. Random Access Get $k$-th (`get(k)`)
7. Full Sequential Iteration (`for (Song s : list)`)

---

## 3. Empirical Results & Measurements

### Benchmark Summary Across Sizes:

| Operation | Size = 1,000 Songs (AL vs LL) | Size = 10,000 Songs (AL vs LL) | Size = 50,000 Songs (AL vs LL) | Decisive Winner |
| :--- | :--- | :--- | :--- | :--- |
| **Add at End** | 0.632 ms vs 0.374 ms | 1.404 ms vs 0.834 ms | 0.772 ms vs 8.701 ms | **ArrayList (11.3x faster at scale)** |
| **Insert at Front** | 0.939 ms vs 0.496 ms (LL 1.9x) | 0.452 ms vs 0.095 ms (LL 4.7x) | 3.193 ms vs 0.074 ms (LL 43.4x) | **LinkedList** ($O(1)$ pointer update) |
| **Insert in Middle** | 0.317 ms vs 2.146 ms (AL 6.8x) | 0.293 ms vs 7.013 ms (AL 24.0x)| 1.677 ms vs 32.263 ms (AL 19.2x)| **ArrayList** (Hardware memory copy beats pointer walking) |
| **Remove from Front**| 0.506 ms vs 0.261 ms (LL 1.9x) | 0.303 ms vs 0.052 ms (LL 5.8x) | 2.733 ms vs 0.038 ms (LL 71.6x)| **LinkedList** |
| **Remove from Middle**| 0.132 ms vs 1.032 ms (AL 7.8x)| 0.246 ms vs 5.870 ms (AL 23.9x)| 1.447 ms vs 31.273 ms (AL 21.6x)| **ArrayList** |
| **Get $k$-th Element** | 0.159 ms vs 1.081 ms (AL 6.8x) | 0.563 ms vs 16.110 ms (AL 28.6x)| 0.282 ms vs 78.307 ms (**AL 278.0x**)| **ArrayList** ($O(1)$ direct offset access) |
| **Iterate Full List**| 0.192 ms vs 0.586 ms (AL 3.1x) | 0.508 ms vs 0.850 ms (AL 1.7x) | 1.104 ms vs 1.199 ms (AL 1.1x) | **ArrayList** (Contiguous spatial cache locality) |

---

## 4. Engineering Analysis & Operating Boundary

### Under what usage pattern is ArrayList a reasonable choice?
- **Read-heavy & Append-heavy Workloads:** When users primarily listen sequentially or shuffle/jump to songs (`get(k)`), `ArrayList` is unbeatable. Its contiguous array representation guarantees $O(1)$ indexed access with zero pointer-chasing and optimal CPU L1/L2 cache prefetching.
- **Middle Modifications:** Interestingly, for middle insertions and deletions, `ArrayList` consistently outperforms `LinkedList` by **19x to 24x**. Although `ArrayList` has to shift elements using `System.arraycopy()`, memory copying is executed via SIMD CPU instructions. Conversely, `LinkedList` must traverse $N/2$ pointers from the head/tail, incurring repeated cache misses.

### When would you stop recommending ArrayList?
- When the application performs **frequent insertions and deletions at the front (index 0)** of a large collection. At $N=50,000$, removing from the front takes **2.73 ms in ArrayList** vs **0.038 ms in LinkedList** (a **71.6x slowdown** due to shifting 50,000 8-byte references on every single deletion).

---

## 5. Changed Condition: The "Up Next" Queue

The streaming service introduces an "Up Next" queue with frequent additions at both ends (`addFirst`, `addLast`) and continuous removals from the front (`pollFirst`).

### Experimental Evaluation (50,000 elements, 10,000 deque operations):
- **ArrayList:** 17.443 ms (1.00x baseline — suffers from massive array shifts on every `add(0)` and `remove(0)`).
- **LinkedList:** 8.919 ms (1.96x speedup — $O(1)$ pointer rewiring, but incurs heavy GC and 24-byte node overhead per song).
- **ArrayDeque:** **1.222 ms (14.27x speedup over ArrayList, 7.3x faster than LinkedList!)**

### Justification for ArrayDeque:
`ArrayDeque` is implemented as a contiguous **circular buffer (ring buffer)** with `head` and `tail` pointers.
- Modifying both ends is pure $O(1)$ pointer arithmetic with **zero memory shifting**.
- Unlike `LinkedList`, it requires no wrapper `Node` objects, maintaining tight memory packing and zero GC allocation overhead during steady-state enqueue/dequeue.

---

## 6. Alternative Concept & Complexity Analysis

### Complexity Comparison Table:

| Operation | ArrayList | LinkedList | ArrayDeque | Plain Array (Manual Shift) |
| :--- | :--- | :--- | :--- | :--- |
| **Add at End** | $O(1)$ amortized | $O(1)$ | $O(1)$ amortized | $O(1)$ if capacity exists, else $O(n)$ resize |
| **Insert at Front** | $O(n)$ (shifts $n$ elements) | $O(1)$ (new node) | $O(1)$ amortized (decrement head) | $O(n)$ (manual `for` loop / `System.arraycopy`) |
| **Insert in Middle** | $O(n)$ (shifts $n/2$ elements) | $O(n)$ (traverse $n/2$ nodes) | Not supported (no indexed insert) | $O(n)$ manual shift |
| **Get $k$-th** | $O(1)$ direct index | $O(n)$ node traversal | $O(1)$ (if exposed via index) | $O(1)$ direct index `arr[k]` |
| **Iterate** | $O(n)$ contiguous scan | $O(n)$ pointer hopping | $O(n)$ circular scan | $O(n)$ direct array loop |
| **Space Overhead** | 8 bytes per slot (ref array) | **24–32 bytes per node** (item + 2 pointers) | 8 bytes per slot | 8 bytes per slot (minimum overhead) |

### Plain Array with Manual Shifting:
If a plain array `Song[]` were used:
- The programmer must manually maintain an `int size` variable, write capacity growth logic (`Arrays.copyOf`), and manually execute `System.arraycopy(arr, index, arr, index + 1, size - index)`.
- Its time and space complexity matches `ArrayList` exactly because `ArrayList` is fundamentally an automated, generic wrapper around an `Object[]` array.

---

## 7. How to Compile and Run
```bash
# Compile
javac Task02/PlaylistBenchmark.java

# Run and inspect results
java -cp Task02 PlaylistBenchmark
```
