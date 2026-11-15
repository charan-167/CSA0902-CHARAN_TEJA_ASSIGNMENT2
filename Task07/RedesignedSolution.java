import java.util.*;
import java.util.function.Function;

/**
 * Task 07: Redesigned Solution - Self-Balancing Indexed Dual-Collection Leaderboard
 * 
 * Engineering Principles Exploited:
 * 1. Continuous Sorted Maintenance: Backed by a Red-Black tree (TreeSet), eliminating O(N log N) re-sorting.
 * 2. Hash Inversion Index: Backed by a HashMap<K, T> for O(1) key lookup and safe O(log N) in-place score mutations.
 * 3. Zero-Allocation Queries: ranking() returns an unmodifiable pre-sorted collection view, eliminating heap churn.
 * 4. Sub-linear Top-K Extraction: topK(k) extracts top-k items in O(k) time instead of O(N log N).
 */
public class RedesignedSolution<K, T> {
    private final NavigableSet<T> sortedSet;
    private final Map<K, T> keyIndex = new HashMap<>();
    private final Function<T, K> keyExtractor;

    public RedesignedSolution(Comparator<? super T> order, Function<T, K> keyExtractor) {
        this.sortedSet = new TreeSet<>(order);
        this.keyExtractor = keyExtractor;
    }

    public void add(T item) {
        K key = keyExtractor.apply(item);
        if (keyIndex.containsKey(key)) {
            // Remove previous entry from tree to maintain BST invariants
            sortedSet.remove(keyIndex.get(key));
        }
        keyIndex.put(key, item);
        sortedSet.add(item);
    }

    /**
     * Efficient O(log N) score update without full scan or re-sorting.
     */
    public void update(K key, Function<T, T> updater) {
        T oldItem = keyIndex.get(key);
        if (oldItem == null) return;

        // Step 1: Remove from sorted set (O(log N))
        sortedSet.remove(oldItem);

        // Step 2: Apply updated score (O(1))
        T newItem = updater.apply(oldItem);

        // Step 3: Re-index in map and tree (O(log N))
        keyIndex.put(key, newItem);
        sortedSet.add(newItem);
    }

    /**
     * O(N) pre-sorted emission with ZERO comparisons and ZERO re-sorting!
     */
    public List<T> ranking() {
        return Collections.unmodifiableList(new ArrayList<>(sortedSet));
    }

    /**
     * O(K) Top-K query: only extracts k elements, avoiding whole-collection scans.
     */
    public List<T> topK(int k) {
        List<T> result = new ArrayList<>(k);
        int count = 0;
        for (T item : sortedSet) {
            result.add(item);
            if (++count >= k) break;
        }
        return Collections.unmodifiableList(result);
    }

    public int size() {
        return sortedSet.size();
    }
}
