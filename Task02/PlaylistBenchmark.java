import java.util.*;

/**
 * Task 02: Music Playlist Editor Benchmark
 * Rigorous empirical comparison of ArrayList, LinkedList, and ArrayDeque.
 * 
 * Author: K V Charan Teja (192424167)
 * Course: Programming in Java - Assignment 2 (CO2)
 */
public class PlaylistBenchmark {

    static class Song {
        final String title;
        final String artist;
        final int durationSeconds;

        Song(String title, String artist, int durationSeconds) {
            this.title = title;
            this.artist = artist;
            this.durationSeconds = durationSeconds;
        }

        @Override
        public String toString() {
            return title + " - " + artist + " (" + durationSeconds + "s)";
        }
    }

    private static Song createSong(int id) {
        return new Song("Track_" + id, "Artist_" + (id % 50), 180 + (id % 120));
    }

    public static void benchmarkList(int initialSize) {
        System.out.println("================================================================================");
        System.out.printf("PLAYLIST BENCHMARK: Size = %,d Songs\n", initialSize);
        System.out.println("================================================================================");

        // Prepopulate baseline data
        List<Song> baseData = new ArrayList<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            baseData.add(createSong(i));
        }

        // Operation 1: Add at End (10,000 ops or initialSize)
        int addOps = Math.min(10000, initialSize);
        long start, end;

        // ArrayList Add at End
        List<Song> arrayList = new ArrayList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < addOps; i++) {
            arrayList.add(createSong(initialSize + i));
        }
        end = System.nanoTime();
        double alAddEndMs = (end - start) / 1_000_000.0;

        // LinkedList Add at End
        List<Song> linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < addOps; i++) {
            linkedList.add(createSong(initialSize + i));
        }
        end = System.nanoTime();
        double llAddEndMs = (end - start) / 1_000_000.0;

        // Operation 2: Insert at Front (index 0) - test 1,000 ops (due to O(N) cost)
        int frontOps = Math.min(1000, initialSize);
        arrayList = new ArrayList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < frontOps; i++) {
            arrayList.add(0, createSong(initialSize + i));
        }
        end = System.nanoTime();
        double alInsertFrontMs = (end - start) / 1_000_000.0;

        linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < frontOps; i++) {
            linkedList.add(0, createSong(initialSize + i));
        }
        end = System.nanoTime();
        double llInsertFrontMs = (end - start) / 1_000_000.0;

        // Operation 3: Insert in Middle (index size/2) - test 1,000 ops
        int midOps = Math.min(1000, initialSize);
        arrayList = new ArrayList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < midOps; i++) {
            arrayList.add(arrayList.size() / 2, createSong(initialSize + i));
        }
        end = System.nanoTime();
        double alInsertMidMs = (end - start) / 1_000_000.0;

        linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < midOps; i++) {
            linkedList.add(linkedList.size() / 2, createSong(initialSize + i));
        }
        end = System.nanoTime();
        double llInsertMidMs = (end - start) / 1_000_000.0;

        // Operation 4: Remove from Front (index 0) - test 1,000 ops
        int remFrontOps = Math.min(1000, initialSize);
        arrayList = new ArrayList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < remFrontOps; i++) {
            arrayList.remove(0);
        }
        end = System.nanoTime();
        double alRemFrontMs = (end - start) / 1_000_000.0;

        linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < remFrontOps; i++) {
            linkedList.remove(0);
        }
        end = System.nanoTime();
        double llRemFrontMs = (end - start) / 1_000_000.0;

        // Operation 5: Remove from Middle (index size/2) - test 1,000 ops
        int remMidOps = Math.min(1000, initialSize);
        arrayList = new ArrayList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < remMidOps; i++) {
            arrayList.remove(arrayList.size() / 2);
        }
        end = System.nanoTime();
        double alRemMidMs = (end - start) / 1_000_000.0;

        linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < remMidOps; i++) {
            linkedList.remove(linkedList.size() / 2);
        }
        end = System.nanoTime();
        double llRemMidMs = (end - start) / 1_000_000.0;

        // Operation 6: Access k-th element (Random Access get(k)) - test 5,000 gets
        int getOps = Math.min(5000, initialSize);
        Random rng = new Random(42);
        int[] indices = new int[getOps];
        for (int i = 0; i < getOps; i++) {
            indices[i] = rng.nextInt(initialSize);
        }

        arrayList = new ArrayList<>(baseData);
        start = System.nanoTime();
        long alDummy = 0;
        for (int i = 0; i < getOps; i++) {
            alDummy += arrayList.get(indices[i]).durationSeconds;
        }
        end = System.nanoTime();
        double alGetMs = (end - start) / 1_000_000.0;

        linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        long llDummy = 0;
        for (int i = 0; i < getOps; i++) {
            llDummy += linkedList.get(indices[i]).durationSeconds;
        }
        end = System.nanoTime();
        double llGetMs = (end - start) / 1_000_000.0;

        // Operation 7: Sequential Iteration (Traverse whole list)
        arrayList = new ArrayList<>(baseData);
        start = System.nanoTime();
        long iterCountAl = 0;
        for (Song s : arrayList) {
            iterCountAl += s.durationSeconds;
        }
        end = System.nanoTime();
        double alIterMs = (end - start) / 1_000_000.0;

        linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        long iterCountLl = 0;
        for (Song s : linkedList) {
            iterCountLl += s.durationSeconds;
        }
        end = System.nanoTime();
        double llIterMs = (end - start) / 1_000_000.0;

        System.out.printf("%-26s | %-16s | %-16s | %-16s\n", "Operation Tested", "ArrayList (ms)", "LinkedList (ms)", "Winner / Ratio");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-26s | %16.3f | %16.3f | %-16s\n", "Add at End (" + addOps + " ops)", alAddEndMs, llAddEndMs, (alAddEndMs < llAddEndMs ? "ArrayList (" + String.format("%.1fx", llAddEndMs / alAddEndMs) + ")" : "LinkedList"));
        System.out.printf("%-26s | %16.3f | %16.3f | %-16s\n", "Insert at Front (" + frontOps + " ops)", alInsertFrontMs, llInsertFrontMs, (llInsertFrontMs < alInsertFrontMs ? "LinkedList (" + String.format("%.1fx", alInsertFrontMs / llInsertFrontMs) + ")" : "ArrayList"));
        System.out.printf("%-26s | %16.3f | %16.3f | %-16s\n", "Insert at Mid (" + midOps + " ops)", alInsertMidMs, llInsertMidMs, (alInsertMidMs < llInsertMidMs ? "ArrayList (" + String.format("%.1fx", llInsertMidMs / alInsertMidMs) + ")" : "LinkedList"));
        System.out.printf("%-26s | %16.3f | %16.3f | %-16s\n", "Remove from Front (" + remFrontOps + " ops)", alRemFrontMs, llRemFrontMs, (llRemFrontMs < alRemFrontMs ? "LinkedList (" + String.format("%.1fx", alRemFrontMs / llRemFrontMs) + ")" : "ArrayList"));
        System.out.printf("%-26s | %16.3f | %16.3f | %-16s\n", "Remove from Mid (" + remMidOps + " ops)", alRemMidMs, llRemMidMs, (alRemMidMs < llRemMidMs ? "ArrayList (" + String.format("%.1fx", llRemMidMs / alRemMidMs) + ")" : "LinkedList"));
        System.out.printf("%-26s | %16.3f | %16.3f | %-16s\n", "Get k-th Element (" + getOps + " ops)", alGetMs, llGetMs, (alGetMs < llGetMs ? "ArrayList (" + String.format("%.1fx", llGetMs / alGetMs) + ")" : "LinkedList"));
        System.out.printf("%-26s | %16.3f | %16.3f | %-16s\n", "Iterate Full List (1 pass)", alIterMs, llIterMs, (alIterMs < llIterMs ? "ArrayList (" + String.format("%.1fx", llIterMs / alIterMs) + ")" : "LinkedList"));
        System.out.println();
    }

    public static void benchmarkUpNextQueue(int queueSize) {
        System.out.println("================================================================================");
        System.out.printf("CHANGED CONDITION: 'Up Next' Queue Benchmark (Size = %,d, Ops = 10,000)\n", queueSize);
        System.out.println("Frequent additions at both ends (addFirst, addLast) and removals from front (pollFirst)");
        System.out.println("================================================================================");

        int ops = 10000;
        List<Song> baseData = new ArrayList<>(queueSize);
        for (int i = 0; i < queueSize; i++) baseData.add(createSong(i));

        // 1. ArrayList as Deque simulation (add(0), add(), remove(0))
        List<Song> arrayList = new ArrayList<>(baseData);
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            if (i % 3 == 0) arrayList.add(0, createSong(i));       // add front
            else if (i % 3 == 1) arrayList.add(createSong(i));     // add end
            else if (!arrayList.isEmpty()) arrayList.remove(0);    // remove front
        }
        long end = System.nanoTime();
        double alTimeMs = (end - start) / 1_000_000.0;

        // 2. LinkedList as Deque
        Deque<Song> linkedList = new LinkedList<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            if (i % 3 == 0) linkedList.addFirst(createSong(i));
            else if (i % 3 == 1) linkedList.addLast(createSong(i));
            else if (!linkedList.isEmpty()) linkedList.pollFirst();
        }
        end = System.nanoTime();
        double llTimeMs = (end - start) / 1_000_000.0;

        // 3. ArrayDeque
        Deque<Song> arrayDeque = new ArrayDeque<>(baseData);
        start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            if (i % 3 == 0) arrayDeque.addFirst(createSong(i));
            else if (i % 3 == 1) arrayDeque.addLast(createSong(i));
            else if (!arrayDeque.isEmpty()) arrayDeque.pollFirst();
        }
        end = System.nanoTime();
        double adTimeMs = (end - start) / 1_000_000.0;

        System.out.printf("%-18s | %-16s | %-16s | %-20s\n", "Implementation", "Time (ms)", "Speedup vs AL", "Memory Model");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-18s | %16.3f | %16s | %-20s\n", "ArrayList", alTimeMs, "1.00x", "Shifts entire array");
        System.out.printf("%-18s | %16.3f | %15.2fx | %-20s\n", "LinkedList", llTimeMs, (alTimeMs / llTimeMs), "Allocates 24B Node/item");
        System.out.printf("%-18s | %16.3f | %15.2fx | %-20s\n", "ArrayDeque", adTimeMs, (alTimeMs / adTimeMs), "Circular array, 0 shifts");
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("     CS09 JAVA ASSIGNMENT 2 - TASK 02: ARRAYLIST vs LINKEDLIST vs ARRAYDEQUE   ");
        System.out.println("     Student: K V Charan Teja (192424167) | Java: " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        // Warmup
        for (int i = 0; i < 2; i++) {
            List<Song> al = new ArrayList<>();
            List<Song> ll = new LinkedList<>();
            for (int j = 0; j < 1000; j++) { al.add(createSong(j)); ll.add(createSong(j)); }
        }

        // Test at 3 playlist sizes
        benchmarkList(1000);
        benchmarkList(10000);
        benchmarkList(50000);

        // Test Changed Condition ("Up Next" Queue)
        benchmarkUpNextQueue(50000);
    }
}
