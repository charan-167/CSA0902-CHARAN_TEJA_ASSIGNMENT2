import java.util.*;

/**
 * Task 07: Empirical Validation of Original vs Redesigned Leaderboard
 * 
 * Author: K V Charan Teja (192424167)
 * Course: Programming in Java - Assignment 2 (CO2)
 */
public class ValidationBenchmark {

    static class TeamRecord {
        final String name;
        final int points;
        final int goalDiff;

        TeamRecord(String name, int points, int goalDiff) {
            this.name = name;
            this.points = points;
            this.goalDiff = goalDiff;
        }

        TeamRecord withPoints(int newPoints, int newGd) {
            return new TeamRecord(this.name, newPoints, newGd);
        }

        @Override
        public String toString() {
            return name + "(" + points + "," + goalDiff + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TeamRecord)) return false;
            return Objects.equals(name, ((TeamRecord) o).name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   CS09 JAVA ASSIGNMENT 2 - TASK 07: DAY-4 DESIGN CHALLENGE VALIDATION        ");
        System.out.println("   Student: K V Charan Teja (192424167) | Java: " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        Comparator<TeamRecord> comparator = Comparator
                .comparingInt((TeamRecord t) -> t.points).reversed()
                .thenComparing(Comparator.comparingInt((TeamRecord t) -> t.goalDiff).reversed())
                .thenComparing((TeamRecord t) -> t.name);

        int N = 10000; // 10,000 teams
        int numReadQueries = 200; // 200 repeated ranking requests
        System.out.printf("Dataset: %,d Teams | Read Queries: %,d requests\n\n", N, numReadQueries);

        // Prepare synthetic teams
        List<TeamRecord> rawTeams = new ArrayList<>(N);
        Random rng = new Random(42);
        for (int i = 0; i < N; i++) {
            rawTeams.add(new TeamRecord("Team_" + String.format("%06d", i), rng.nextInt(100), rng.nextInt(50) - 25));
        }

        // =====================================================================
        // EXPERIMENT 1: Repeated Read Queries (ranking())
        // =====================================================================
        System.out.println("--- EXPERIMENT 1: Read Queries Under Load (200 ranking() calls on 10k items) ---");

        // 1A. Original Solution
        System.gc();
        long memBeforeOrig = getUsedMemory();
        OriginalSolution<TeamRecord> orig = new OriginalSolution<>(comparator);
        for (TeamRecord t : rawTeams) orig.add(t);

        long startOrigRead = System.nanoTime();
        int origDummyCount = 0;
        for (int q = 0; q < numReadQueries; q++) {
            List<TeamRecord> r = orig.ranking();
            origDummyCount += r.size();
        }
        long endOrigRead = System.nanoTime();
        long memAfterOrig = getUsedMemory();
        double origReadMs = (endOrigRead - startOrigRead) / 1_000_000.0;
        double origMemMb = Math.max(0, memAfterOrig - memBeforeOrig) / (1024.0 * 1024.0);

        // 1B. Redesigned Solution
        System.gc();
        long memBeforeRedesign = getUsedMemory();
        RedesignedSolution<String, TeamRecord> redesign = new RedesignedSolution<>(comparator, t -> t.name);
        for (TeamRecord t : rawTeams) redesign.add(t);

        long startRedesignRead = System.nanoTime();
        int redesignDummyCount = 0;
        for (int q = 0; q < numReadQueries; q++) {
            List<TeamRecord> r = redesign.ranking();
            redesignDummyCount += r.size();
        }
        long endRedesignRead = System.nanoTime();
        long memAfterRedesign = getUsedMemory();
        double redesignReadMs = (endRedesignRead - startRedesignRead) / 1_000_000.0;
        double redesignMemMb = Math.max(0, memAfterRedesign - memBeforeRedesign) / (1024.0 * 1024.0);

        System.out.printf("%-20s | %-16s | %-16s | %-16s\n", "Solution", "Total Time (ms)", "Time / Query (ms)", "Speedup vs Original");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-20s | %16.2f | %16.3f | %16s\n", "Original Solution", origReadMs, origReadMs / numReadQueries, "1.00x (Baseline)");
        System.out.printf("%-20s | %16.2f | %16.3f | %15.2fx\n", "Redesigned Solution", redesignReadMs, redesignReadMs / numReadQueries, (origReadMs / redesignReadMs));
        System.out.println();

        // =====================================================================
        // EXPERIMENT 2: Top-10 Queries (topK(10) vs Full ranking().subList(0, 10))
        // =====================================================================
        System.out.println("--- EXPERIMENT 2: Interactive Top-10 Query (500 requests) ---");
        int topKRequests = 500;

        // Original: sorts 10,000 items, then takes sublist(0, 10)
        long startOrigTop10 = System.nanoTime();
        for (int i = 0; i < topKRequests; i++) {
            List<TeamRecord> top10 = orig.ranking().subList(0, 10);
        }
        long endOrigTop10 = System.nanoTime();
        double origTop10Ms = (endOrigTop10 - startOrigTop10) / 1_000_000.0;

        // Redesign: extracts only 10 items via iterator
        long startRedesignTop10 = System.nanoTime();
        for (int i = 0; i < topKRequests; i++) {
            List<TeamRecord> top10 = redesign.topK(10);
        }
        long endRedesignTop10 = System.nanoTime();
        double redesignTop10Ms = (endRedesignTop10 - startRedesignTop10) / 1_000_000.0;

        System.out.printf("%-20s | %-16s | %-16s | %-16s\n", "Solution", "500 Top-10 Ops", "Time / Query", "Speedup");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-20s | %13.2f ms | %13.4f ms | %16s\n", "Original Solution", origTop10Ms, origTop10Ms / topKRequests, "1.00x");
        System.out.printf("%-20s | %13.2f ms | %13.4f ms | %15.2fx\n", "Redesigned Solution", redesignTop10Ms, redesignTop10Ms / topKRequests, (origTop10Ms / redesignTop10Ms));
        System.out.println();

        // =====================================================================
        // EXPERIMENT 3: Mixed Production Workload (80% Read Queries, 20% Score Updates)
        // =====================================================================
        System.out.println("--- EXPERIMENT 3: Mixed Workload (400 Reads + 100 Score Updates) ---");

        long startMixed = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            if (i % 5 == 0) {
                // Score update
                String target = "Team_" + String.format("%06d", rng.nextInt(N));
                redesign.update(target, t -> t.withPoints(t.points + 3, t.goalDiff + 2));
            } else {
                // Read query
                redesign.topK(10);
            }
        }
        long endMixed = System.nanoTime();
        double mixedMs = (endMixed - startMixed) / 1_000_000.0;

        System.out.printf("Redesigned Mixed Workload (500 ops) Total Execution Time: %.2f ms (%.3f ms / op)\n",
                mixedMs, mixedMs / 500.0);

        // Verification of ranking correctness
        List<TeamRecord> origRank = orig.ranking();
        List<TeamRecord> redesignRank = redesign.ranking();
        System.out.println("\nCorrectness Verification: First 3 Teams Match?");
        System.out.println("  Original Top 3:   " + origRank.subList(0, 3));
        System.out.println("  Redesign Top 3:   " + redesignRank.subList(0, 3));
    }
}
