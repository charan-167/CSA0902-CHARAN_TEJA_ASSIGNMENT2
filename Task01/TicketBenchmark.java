import java.util.*;

/**
 * Task 01: Railway e-Ticket Generation Benchmark
 * Comparing String (+), StringBuilder, and StringBuffer under different workloads.
 * 
 * Author: K V Charan Teja (192424167)
 * Course: Programming in Java - Assignment 2 (CO2)
 */
public class TicketBenchmark {

    // Sample field names and values for e-ticket generation
    private static final String[] FIELD_NAMES = {
        "PNR", "TrainNo", "TrainName", "Origin", "Destination", "Class", "Quota",
        "DateOfJourney", "DepartureTime", "ArrivalTime", "DistanceKm", "BookingDate",
        "TotalFare", "BaseFare", "ReservationFee", "SuperfastCharge", "TatkalCharge",
        "GST", "PaymentMode", "TransactionId", "Passenger1_Name", "Passenger1_Age",
        "Passenger1_Gender", "Passenger1_Berth", "Passenger1_Coach", "Passenger1_Status",
        "Passenger2_Name", "Passenger2_Age", "Passenger2_Gender", "Passenger2_Berth",
        "Passenger2_Coach", "Passenger2_Status", "Passenger3_Name", "Passenger3_Age",
        "Passenger3_Gender", "Passenger3_Berth", "Passenger3_Coach", "Passenger3_Status",
        "ChartStatus", "FoodChoice", "InsuranceOpted", "InsurancePolicyNo", "IRCTCRefNo",
        "GateNo", "PlatformNo", "SecurityCode", "QRData", "TermsAccepted", "AgentId", "Remarks"
    };

    // Approach 1: String concatenation with + operator
    public static String buildTicketWithString(List<String> fields) {
        String ticket = "";
        for (int i = 0; i < fields.size(); i++) {
            ticket = ticket + fields.get(i) + "\n";
        }
        return ticket;
    }

    // Approach 2: StringBuilder (unsynchronized, single-thread optimal)
    public static String buildTicketWithStringBuilder(List<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            sb.append(fields.get(i)).append("\n");
        }
        return sb.toString();
    }

    // Approach 3: StringBuffer (synchronized, thread-safe)
    public static String buildTicketWithStringBuffer(List<String> fields) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fields.size(); i++) {
            sb.append(fields.get(i)).append("\n");
        }
        return sb.toString();
    }

    // Alternative Approach: String.join
    public static String buildTicketWithStringJoin(List<String> fields) {
        return String.join("\n", fields) + "\n";
    }

    // Generate fields for a ticket
    public static List<String> generateFields(int numFields, int ticketIndex) {
        List<String> fields = new ArrayList<>(numFields);
        for (int i = 0; i < numFields; i++) {
            String fieldName = (i < FIELD_NAMES.length) ? FIELD_NAMES[i] : "CustomField_" + i;
            fields.add(fieldName + ": VAL-" + ticketIndex + "-" + i);
        }
        return fields;
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static void runWorkload(String workloadName, int numTickets, int numFieldsPerTicket, int runs) {
        System.out.println("================================================================================");
        System.out.printf("WORKLOAD: %s [%d Tickets, %d Fields/Ticket, %d Repeated Runs]\n",
                workloadName, numTickets, numFieldsPerTicket, runs);
        System.out.println("================================================================================");

        List<List<String>> allTicketsFields = new ArrayList<>(numTickets);
        for (int i = 0; i < numTickets; i++) {
            allTicketsFields.add(generateFields(numFieldsPerTicket, i));
        }

        // Theoretical calculation of intermediate string objects:
        // For String (+): each iteration of the loop creates 1 new String object (and internally StringBuilder in some compilers).
        // For n fields: n intermediate String objects per ticket. Total = numTickets * numFields.
        // For StringBuilder: 0 intermediate strings during loop, 1 final String created by toString(). Total = numTickets.
        // For StringBuffer: 0 intermediate strings during loop, 1 final String created by toString(). Total = numTickets.
        long stringIntermediateObjects = (long) numTickets * numFieldsPerTicket;
        long builderIntermediateObjects = numTickets;

        System.out.printf("Estimated Intermediate String Objects Created:\n");
        System.out.printf("  - String (+):          %,d objects\n", stringIntermediateObjects);
        System.out.printf("  - StringBuilder:       %,d objects (only final output strings)\n", builderIntermediateObjects);
        System.out.printf("  - StringBuffer:        %,d objects (only final output strings)\n\n", builderIntermediateObjects);

        // Benchmark String (+)
        double totalTimeStringMs = 0;
        long memUsedString = 0;
        for (int r = 0; r < runs; r++) {
            System.gc();
            long memBefore = getUsedMemory();
            long start = System.nanoTime();
            for (int i = 0; i < numTickets; i++) {
                String res = buildTicketWithString(allTicketsFields.get(i));
            }
            long end = System.nanoTime();
            long memAfter = getUsedMemory();
            totalTimeStringMs += (end - start) / 1_000_000.0;
            memUsedString += Math.max(0, memAfter - memBefore);
        }
        double avgTimeString = totalTimeStringMs / runs;
        double avgMemStringMb = (memUsedString / (double) runs) / (1024.0 * 1024.0);

        // Benchmark StringBuilder
        double totalTimeBuilderMs = 0;
        long memUsedBuilder = 0;
        for (int r = 0; r < runs; r++) {
            System.gc();
            long memBefore = getUsedMemory();
            long start = System.nanoTime();
            for (int i = 0; i < numTickets; i++) {
                String res = buildTicketWithStringBuilder(allTicketsFields.get(i));
            }
            long end = System.nanoTime();
            long memAfter = getUsedMemory();
            totalTimeBuilderMs += (end - start) / 1_000_000.0;
            memUsedBuilder += Math.max(0, memAfter - memBefore);
        }
        double avgTimeBuilder = totalTimeBuilderMs / runs;
        double avgMemBuilderMb = (memUsedBuilder / (double) runs) / (1024.0 * 1024.0);

        // Benchmark StringBuffer
        double totalTimeBufferMs = 0;
        long memUsedBuffer = 0;
        for (int r = 0; r < runs; r++) {
            System.gc();
            long memBefore = getUsedMemory();
            long start = System.nanoTime();
            for (int i = 0; i < numTickets; i++) {
                String res = buildTicketWithStringBuffer(allTicketsFields.get(i));
            }
            long end = System.nanoTime();
            long memAfter = getUsedMemory();
            totalTimeBufferMs += (end - start) / 1_000_000.0;
            memUsedBuffer += Math.max(0, memAfter - memBefore);
        }
        double avgTimeBuffer = totalTimeBufferMs / runs;
        double avgMemBufferMb = (memUsedBuffer / (double) runs) / (1024.0 * 1024.0);

        // Benchmark String.join
        double totalTimeJoinMs = 0;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < numTickets; i++) {
                String res = buildTicketWithStringJoin(allTicketsFields.get(i));
            }
            long end = System.nanoTime();
            totalTimeJoinMs += (end - start) / 1_000_000.0;
        }
        double avgTimeJoin = totalTimeJoinMs / runs;

        System.out.printf("%-18s | %-16s | %-16s | %-16s\n", "Implementation", "Avg Time (ms)", "Speedup vs String", "Est. Heap (MB)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-18s | %16.3f | %16s | %16.2f\n", "String (+)", avgTimeString, "1.00x (Baseline)", avgMemStringMb);
        System.out.printf("%-18s | %16.3f | %15.2fx | %16.2f\n", "StringBuilder", avgTimeBuilder, (avgTimeString / avgTimeBuilder), avgMemBuilderMb);
        System.out.printf("%-18s | %16.3f | %15.2fx | %16.2f\n", "StringBuffer", avgTimeBuffer, (avgTimeString / avgTimeBuffer), avgMemBufferMb);
        System.out.printf("%-18s | %16.3f | %15.2fx | %16s\n", "String.join()", avgTimeJoin, (avgTimeString / avgTimeJoin), "N/A");
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("  CS09 JAVA ASSIGNMENT 2 - TASK 01: STRING vs STRINGBUILDER vs STRINGBUFFER   ");
        System.out.println("  Student: K V Charan Teja (192424167) | Java Version: " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        // Warm up JVM JIT compiler
        System.out.println("Warming up JVM JIT compiler...");
        List<String> warmupFields = generateFields(20, 0);
        for (int i = 0; i < 5000; i++) {
            buildTicketWithString(warmupFields);
            buildTicketWithStringBuilder(warmupFields);
            buildTicketWithStringBuffer(warmupFields);
        }
        System.out.println("Warmup complete.\n");

        // Workload 1: 1 Short Ticket with 10 fields (Repeated over 1,000 iterations to measure accurately)
        runWorkload("Workload 1 (Short Ticket - 10 Fields)", 1, 10, 1000);

        // Workload 2: 10,000 Tickets with 50 fields each (Heavy standard load)
        runWorkload("Workload 2 (Standard Bulk - 10,000 Tickets x 50 Fields)", 10000, 50, 5);

        // Workload 3: Custom Workload: 5,000 Large Enterprise Tickets with 100 fields each
        runWorkload("Workload 3 (Custom Enterprise - 5,000 Tickets x 100 Fields)", 5000, 100, 5);

        // Sample ticket preview
        System.out.println("Sample Generated Ticket Preview (first 5 fields):");
        List<String> sampleFields = generateFields(5, 1001);
        System.out.println(buildTicketWithStringBuilder(sampleFields));
    }
}
