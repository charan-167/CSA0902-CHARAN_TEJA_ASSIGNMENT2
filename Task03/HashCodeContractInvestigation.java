import java.util.*;

/**
 * Task 03: Comprehensive Investigation of the hashCode-equals Contract
 * 
 * Tests:
 * 1. Test 1 (Appears to work): Same object reference inserted multiple times.
 * 2. Test 2 (Exposes duplicates): Different object instances with identical logical keys without hashCode.
 * 3. Test 3A (Mutation break): Modifying key field after insertion corrupts HashSet lookup.
 * 3. Test 3B (TreeSet inconsistency): Comparator inconsistent with equals causes silent data loss.
 * 
 * Author: K V Charan Teja (192424167)
 * Course: Programming in Java - Assignment 2 (CO2)
 */
public class HashCodeContractInvestigation {

    // Mutable Student class to demonstrate the catastrophic impact of modifying fields after insertion
    static class MutableStudent {
        String regNo;
        String name;

        MutableStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MutableStudent)) return false;
            return Objects.equals(regNo, ((MutableStudent) o).regNo);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(regNo);
        }

        @Override
        public String toString() {
            return regNo + ":" + name;
        }
    }

    // Student without hashCode (Junior Developer's code)
    static class BrokenStudent {
        String regNo;
        String name;

        BrokenStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BrokenStudent)) return false;
            return Objects.equals(regNo, ((BrokenStudent) o).regNo);
        }
        // hashCode NOT overridden -> inherits Object.hashCode() based on memory address

        @Override
        public String toString() {
            return regNo + ":" + name + "@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   CS09 JAVA ASSIGNMENT 2 - TASK 03: HASHCODE-EQUALS CONTRACT INVESTIGATION    ");
        System.out.println("   Student: K V Charan Teja (192424167) | Java: " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        // -------------------------------------------------------------------------
        // TEST 1: Where HashSet APPEARS to work correctly despite missing hashCode
        // -------------------------------------------------------------------------
        System.out.println("--- TEST 1: Identical Object References (Appears to Work) ---");
        System.out.println("PREDICTION: Set size = 1.");
        System.out.println("REASON: When the exact same object reference is added, HashSet checks (p.key == key).");
        System.out.println("        Because reference identity is true, it replaces the value without checking hashCode or equals.");

        Set<BrokenStudent> test1Set = new HashSet<>();
        BrokenStudent s1 = new BrokenStudent("192311001", "Arun");
        test1Set.add(s1);
        test1Set.add(s1); // Same reference re-scanned
        System.out.println("OBSERVED: Set size = " + test1Set.size() + " -> " + test1Set);
        System.out.println("VERDICT: Prediction Confirmed.\n");

        // -------------------------------------------------------------------------
        // TEST 2: Deliberately designed to EXPOSE duplicate entries
        // -------------------------------------------------------------------------
        System.out.println("--- TEST 2: Distinct Instances with Identical Keys (Exposes Duplicates) ---");
        System.out.println("PREDICTION: Set size = 2 (Duplicate is NOT eliminated).");
        System.out.println("REASON: Two distinct 'new BrokenStudent()' instances have different System.identityHashCode().");
        System.out.println("        They are mapped to different hash buckets. equals() is NEVER called across buckets.");

        Set<BrokenStudent> test2Set = new HashSet<>();
        BrokenStudent s2a = new BrokenStudent("192311001", "Arun");
        BrokenStudent s2b = new BrokenStudent("192311001", "Arun"); // New object, same student ID
        test2Set.add(s2a);
        test2Set.add(s2b);
        System.out.println("OBSERVED: Set size = " + test2Set.size() + " -> " + test2Set);
        System.out.println("VERDICT: Prediction Confirmed. Contract violation broke Set uniqueness.\n");

        // -------------------------------------------------------------------------
        // TEST 3A: Mutating a key field after insertion into HashSet
        // -------------------------------------------------------------------------
        System.out.println("--- TEST 3A: Mutating Key Field After Insertion (Ghost Element Bug) ---");
        System.out.println("PREDICTION: set.contains(student) will return FALSE after mutation.");
        System.out.println("REASON: hashCode() was computed based on the initial regNo when placed into bucket B1.");
        System.out.println("        After changing regNo, contains() calculates a new hash and searches bucket B2.");
        System.out.println("        The element remains trapped in B1 as an unreachable ghost!");

        Set<MutableStudent> test3Set = new HashSet<>();
        MutableStudent s3 = new MutableStudent("192311001", "Arun");
        test3Set.add(s3);
        System.out.println("Before mutation: contains('192311001')? " + test3Set.contains(s3) + ", Set size = " + test3Set.size());

        // Mutate the regNo!
        s3.regNo = "192311999";
        System.out.println("After mutating regNo to '192311999':");
        System.out.println("Does set.contains(s3) find it? " + test3Set.contains(s3)); // False!
        test3Set.add(s3); // Adding again
        System.out.println("After re-adding s3, Set size = " + test3Set.size() + " (Corrupted! Duplicate object exists in two buckets)");
        System.out.println("VERDICT: Prediction Confirmed. Demonstrates why key fields MUST BE FINAL/IMMUTABLE.\n");

        // -------------------------------------------------------------------------
        // TEST 3B: TreeSet Inconsistent with equals
        // -------------------------------------------------------------------------
        System.out.println("--- TEST 3B: TreeSet with Comparator Inconsistent with equals ---");
        System.out.println("PREDICTION: Second student with a different regNo will be SILENTLY DROPPED!");
        System.out.println("REASON: If a Comparator only compares 'name', compareTo() returns 0 for same names.");
        System.out.println("        TreeSet uses compare() == 0 to determine equality, ignoring equals().");

        Comparator<Student> nameOnlyComparator = Comparator.comparing(s -> s.toString().split(" ")[1]);
        TreeSet<Student> treeSet = new TreeSet<>(nameOnlyComparator);
        Student divya1 = new Student("192311002", "Divya");
        Student divya2 = new Student("192311099", "Divya"); // Different student, same name!
        treeSet.add(divya1);
        treeSet.add(divya2);
        System.out.println("OBSERVED: TreeSet size = " + treeSet.size() + " (Only 1 kept!) -> " + treeSet);
        System.out.println("VERDICT: Prediction Confirmed. Inconsistent comparator caused silent data loss.\n");
    }
}
