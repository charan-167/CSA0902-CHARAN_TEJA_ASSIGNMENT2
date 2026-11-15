import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Task 07: Original Solution (from Q5 Leaderboard)
 * 
 * Bottleneck:
 * - Backed by a single ArrayList<T>.
 * - Every call to ranking() duplicates the entire list and performs a full O(N log N) sort.
 * - Score updates require an O(N) linear scan to locate the entity.
 */
public class OriginalSolution<T> {
    private final List<T> items = new ArrayList<>();
    private final Comparator<? super T> order;

    public OriginalSolution(Comparator<? super T> order) {
        this.order = order;
    }

    public void add(T item) {
        items.add(item);
    }

    public List<T> ranking() {
        // Unnecessary work: creates a new list and sorts all items from scratch on EVERY call
        List<T> sortedCopy = new ArrayList<>(items);
        sortedCopy.sort(order);
        return sortedCopy;
    }

    public int size() {
        return items.size();
    }
}
