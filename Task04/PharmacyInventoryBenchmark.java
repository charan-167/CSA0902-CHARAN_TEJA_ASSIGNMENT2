import java.util.*;

/**
 * Task 04: Hospital Pharmacy Inventory Map Benchmark
 * Comparing HashMap, TreeMap, and LinkedHashMap across lookups and report generation.
 * 
 * Author: K V Charan Teja (192424167)
 * Course: Programming in Java - Assignment 2 (CO2)
 */
public class PharmacyInventoryBenchmark {

    static class Medicine {
        final String code;
        final String name;
        int stock;
        final double unitPrice;
        final long insertionSequence;

        Medicine(String code, String name, int stock, double unitPrice, long insertionSequence) {
            this.code = code;
            this.name = name;
            this.stock = stock;
            this.unitPrice = unitPrice;
            this.insertionSequence = insertionSequence;
        }

        @Override
        public String toString() {
            return code + ": " + name + " (Stock: " + stock + ")";
        }
    }

    // Format medicine code with padded number so lexicographical sorting matches numerical
    private static String formatCode(int id) {
        return String.format("MED-%07d", id);
    }

    public static void runBenchmark(int inventorySize, int lookupCount) {
        System.out.println("================================================================================");
        System.out.printf("PHARMACY INVENTORY BENCHMARK: %,d Medicines, %,d Lookups\n", inventorySize, lookupCount);
        System.out.println("================================================================================");

        // Generate synthetic medicine data in arbitrary/arrival order
        List<Medicine> medicineList = new ArrayList<>(inventorySize);
        Random rng = new Random(12345);
        List<Integer> ids = new ArrayList<>(inventorySize);
        for (int i = 0; i < inventorySize; i++) ids.add(i);
        Collections.shuffle(ids, rng); // Random arrival order

        for (int seq = 0; seq < inventorySize; seq++) {
            int id = ids.get(seq);
            medicineList.add(new Medicine(formatCode(id), "Drug_" + id, 50 + (id % 200), 10.0 + (id % 50), seq));
        }

        // Pre-generate lookup query keys
        String[] queryKeys = new String[lookupCount];
        for (int i = 0; i < lookupCount; i++) {
            queryKeys[i] = formatCode(rng.nextInt(inventorySize));
        }

        // 1. Populate Maps and measure insertion time
        long start, end;

        // HashMap
        start = System.nanoTime();
        Map<String, Medicine> hashMap = new HashMap<>(inventorySize);
        for (Medicine m : medicineList) hashMap.put(m.code, m);
        end = System.nanoTime();
        double hmPutMs = (end - start) / 1_000_000.0;

        // TreeMap
        start = System.nanoTime();
        Map<String, Medicine> treeMap = new TreeMap<>();
        for (Medicine m : medicineList) treeMap.put(m.code, m);
        end = System.nanoTime();
        double tmPutMs = (end - start) / 1_000_000.0;

        // LinkedHashMap
        start = System.nanoTime();
        Map<String, Medicine> linkedHashMap = new LinkedHashMap<>(inventorySize);
        for (Medicine m : medicineList) linkedHashMap.put(m.code, m);
        end = System.nanoTime();
        double lhmPutMs = (end - start) / 1_000_000.0;

        // 2. Measure Lookups
        start = System.nanoTime();
        long dummyHm = 0;
        for (int i = 0; i < lookupCount; i++) {
            dummyHm += hashMap.get(queryKeys[i]).stock;
        }
        end = System.nanoTime();
        double hmGetMs = (end - start) / 1_000_000.0;
        double hmGetNsPerOp = ((end - start) / (double) lookupCount);

        start = System.nanoTime();
        long dummyTm = 0;
        for (int i = 0; i < lookupCount; i++) {
            dummyTm += treeMap.get(queryKeys[i]).stock;
        }
        end = System.nanoTime();
        double tmGetMs = (end - start) / 1_000_000.0;
        double tmGetNsPerOp = ((end - start) / (double) lookupCount);

        start = System.nanoTime();
        long dummyLhm = 0;
        for (int i = 0; i < lookupCount; i++) {
            dummyLhm += linkedHashMap.get(queryKeys[i]).stock;
        }
        end = System.nanoTime();
        double lhmGetMs = (end - start) / 1_000_000.0;
        double lhmGetNsPerOp = ((end - start) / (double) lookupCount);

        // 3. Report 1: Code-Order Report (Sorted by Medicine Code)
        // HashMap: requires extracting entries into list and sorting
        start = System.nanoTime();
        List<Medicine> hmCodeReport = new ArrayList<>(hashMap.values());
        hmCodeReport.sort(Comparator.comparing(m -> m.code));
        end = System.nanoTime();
        double hmReport1Ms = (end - start) / 1_000_000.0;

        // TreeMap: naturally in code order!
        start = System.nanoTime();
        List<Medicine> tmCodeReport = new ArrayList<>(treeMap.values());
        end = System.nanoTime();
        double tmReport1Ms = (end - start) / 1_000_000.0;

        // LinkedHashMap: requires sorting
        start = System.nanoTime();
        List<Medicine> lhmCodeReport = new ArrayList<>(linkedHashMap.values());
        lhmCodeReport.sort(Comparator.comparing(m -> m.code));
        end = System.nanoTime();
        double lhmReport1Ms = (end - start) / 1_000_000.0;

        // 4. Report 2: Insertion-Order Report (Order added)
        // HashMap: needs sequence number sorting
        start = System.nanoTime();
        List<Medicine> hmSeqReport = new ArrayList<>(hashMap.values());
        hmSeqReport.sort(Comparator.comparingLong(m -> m.insertionSequence));
        end = System.nanoTime();
        double hmReport2Ms = (end - start) / 1_000_000.0;

        // TreeMap: needs sequence number sorting
        start = System.nanoTime();
        List<Medicine> tmSeqReport = new ArrayList<>(treeMap.values());
        tmSeqReport.sort(Comparator.comparingLong(m -> m.insertionSequence));
        end = System.nanoTime();
        double tmReport2Ms = (end - start) / 1_000_000.0;

        // LinkedHashMap: naturally in insertion order!
        start = System.nanoTime();
        List<Medicine> lhmSeqReport = new ArrayList<>(linkedHashMap.values());
        end = System.nanoTime();
        double lhmReport2Ms = (end - start) / 1_000_000.0;

        // Validate ordering correctness
        boolean tmReport1Correct = isSortedByCode(tmCodeReport);
        boolean lhmReport2Correct = isSortedByInsertion(lhmSeqReport);

        System.out.printf("%-15s | %-12s | %-14s | %-12s | %-16s | %-16s\n",
                "Map Type", "Put (ms)", "100k Get (ms)", "ns / Lookup", "Report 1 (Code)", "Report 2 (Arrival)");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %12.3f | %14.3f | %12.1f | %13.3f ms | %13.3f ms\n",
                "HashMap", hmPutMs, hmGetMs, hmGetNsPerOp, hmReport1Ms, hmReport2Ms);
        System.out.printf("%-15s | %12.3f | %14.3f | %12.1f | %13.3f ms | %13.3f ms\n",
                "TreeMap", tmPutMs, tmGetMs, tmGetNsPerOp, tmReport1Ms, tmReport2Ms);
        System.out.printf("%-15s | %12.3f | %14.3f | %12.1f | %13.3f ms | %13.3f ms\n",
                "LinkedHashMap", lhmPutMs, lhmGetMs, lhmGetNsPerOp, lhmReport1Ms, lhmReport2Ms);
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("Verification: TreeMap Code Report naturally sorted? %b | LinkedHashMap Arrival Report naturally ordered? %b\n\n",
                tmReport1Correct, lhmReport2Correct);
    }

    private static boolean isSortedByCode(List<Medicine> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).code.compareTo(list.get(i + 1).code) > 0) return false;
        }
        return true;
    }

    private static boolean isSortedByInsertion(List<Medicine> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).insertionSequence > list.get(i + 1).insertionSequence) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("      CS09 JAVA ASSIGNMENT 2 - TASK 04: HASHMAP vs TREEMAP vs LINKEDHASHMAP    ");
        System.out.println("      Student: K V Charan Teja (192424167) | Java: " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        // Warm up JIT
        runBenchmark(500, 1000);

        // Three distinct inventory sizes tested with 100,000 lookups
        runBenchmark(1000, 100000);
        runBenchmark(10000, 100000);
        runBenchmark(100000, 100000);
    }
}
