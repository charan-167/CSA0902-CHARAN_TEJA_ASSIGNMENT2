import java.util.HashSet;
import java.util.Set;

/**
 * Attendance program WITHOUT completing TODO 1 (hashCode not overridden).
 * Used to record baseline output before fixing the contract.
 */
class StudentUnfixed {
    private final String regNo;
    private final String name;

    StudentUnfixed(String regNo, String name) { this.regNo = regNo; this.name = name; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentUnfixed)) return false;
        return regNo.equals(((StudentUnfixed) o).regNo);
    }

    // TODO 1 NOT COMPLETED: hashCode() is not overridden

    @Override public String toString() { return regNo + " " + name; }
}

public class AttendanceBeforeFix {
    public static void main(String[] args) {
        Set<StudentUnfixed> present = new HashSet<>();
        present.add(new StudentUnfixed("192311001", "Arun"));    // scanned at Gate 1
        present.add(new StudentUnfixed("192311002", "Divya"));
        present.add(new StudentUnfixed("192311001", "Arun"));    // scanned again at Gate 3
        System.out.println("Output BEFORE TODO 1: Unique students = " + present.size());
        System.out.println("Set elements: " + present);
    }
}
