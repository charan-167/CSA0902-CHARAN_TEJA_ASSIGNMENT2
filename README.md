# CSA09-192424167-Assignment2
## PROGRAMMING IN JAVA – ASSIGNMENT 2 (CO2)
### Sessions 15–26: Strings, Regular Expressions, Java Collection Framework, Generics & Comparators

---

## Student & Environment Details
- **Student Name:** K V Charan Teja
- **Register Number:** 192424167
- **Course / Class:** CSA09 - Programming in Java
- **GitHub Repository:** [https://github.com/charan-167/CSA09-192424167-Assignment2](https://github.com/charan-167/CSA09-192424167-Assignment2)
- **Java Version:** `openjdk version "17.0.21.0.2" 2026-10-22 LTS`
- **Operating System:** Windows 11 64-bit

---

## Executive Summary of Completed Tasks

| Task | Topic / Assessed Area | Bloom's Level | Marks | Key Finding / Result |
| :--- | :--- | :--- | :--- | :--- |
| **Task 01** | String, StringBuilder & StringBuffer Under Load | K4 - Analyse | 6 | `StringBuilder` achieved a **9.05x speedup** over `String (+)` on 100-field tickets; eliminated 500,000 intermediate objects. |
| **Task 02** | ArrayList, LinkedList & ArrayDeque (Music Playlist) | K4 - Analyse | 4 | `ArrayList` was **278x faster** for random access ($O(1)$) and 20x faster in middle insertions; `ArrayDeque` was **14.27x faster** for the "Up Next" queue. |
| **Task 03** | Engineer the hashCode–equals Contract (Attendance) | K4 - Analyse | 6 | Fixed TODO 1; set size reduced from 3 to 2 (expected); proved why key mutation causes ghost memory leaks. |
| **Task 04** | Choosing a Map for Pharmacy Inventory | K4 - Analyse | 6 | `HashMap` and `LinkedHashMap` yielded sub-300ns lookups; `LinkedHashMap` generated arrival reports in **1.7 ms** with zero sorting overhead. |
| **Task 05** | Generic Leaderboard Component from Principles | K4 - Analyse | 7 | Completed TODO 1 & 2; proved transitivity and equality consistency; demonstrated integer overflow failure in naive subtraction. |
| **Task 06** | Priority, Order & Fairness in Emergency Triage | K5 - Evaluate | 8 | Completed TODO 1; verified 8-patient clinical order; explained heap array layout; implemented fair aging rule to eliminate starvation. |
| **Task 07** | Day-4 Design Challenge: Diagnose, Redesign, Validate | K5 - Evaluate | 13 | Diagnosed $O(N \log N)$ re-sorting bottleneck in Leaderboard; redesigned with dual-index `TreeSet` + `HashMap`, yielding a **1,194x speedup** on Top-10 queries. |
| **TOTAL** | | | **50** | **All 7 Tasks Fully Implemented, Executed, and Validated** |

---

## Repository Structure
```text
JAVA ASSIGNMENT 2/
├── Task01/                                # Q1: String, StringBuilder & StringBuffer
│   ├── TicketBenchmark.java               # Microbenchmark across 3 contrasting workloads
│   ├── Output.txt                         # Raw execution logs and timing measurements
│   └── README.md                          # Analysis, predictions, and multithreading decision
├── Task02/                                # Q2: ArrayList, LinkedList & ArrayDeque
│   ├── PlaylistBenchmark.java             # Microbenchmark across 1k, 10k, 50k playlist sizes
│   ├── Output.txt                         # Raw execution measurements
│   └── README.md                          # Operating boundaries and "Up Next" evaluation
├── Task03/                                # Q3: hashCode-equals Contract (Attendance)
│   ├── Attendance.java                    # Completed code (TODO 1 resolved)
│   ├── AttendanceBeforeFix.java           # Baseline code showing duplicate defect
│   ├── HashCodeContractInvestigation.java # 3 deliberately designed experimental inquiries
│   ├── Output.txt                         # Program outputs before and after fix
│   └── README.md                          # Contract theory, mutation analysis & complexity
├── Task04/                                # Q4: Hospital Pharmacy Inventory Map
│   ├── PharmacyInventoryBenchmark.java    # 100,000 lookups and evening report benchmarks
│   ├── Output.txt                         # Empirical execution output across 1k, 10k, 100k
│   └── README.md                          # Map selection defense and Hashtable deprecation
├── Task05/                                # Q5: Generic Leaderboard Component
│   ├── LeaderboardDemo.java               # Completed code (TODO 1 & 2 resolved)
│   ├── LeaderboardTest.java               # Tie-breakers, overflow edge cases & generic reuse
│   ├── Output.txt                         # Exact expected output matching
│   └── README.md                          # Comparison principle, transitivity & wildcards
├── Task06/                                # Q6: Emergency Department Triage
│   ├── Triage.java                        # Completed code (TODO 1 resolved)
│   ├── TriageBenchmarkAndFairness.java    # 8-patient check, heap array analysis & fair aging
│   ├── Output.txt                         # Output logs and city hospital feasibility
│   └── README.md                          # Heap tree mechanics, starvation & fairness rule
├── Task07/                                # Q7: Day-4 Design Challenge
│   ├── OriginalSolution.java              # Baseline O(N log N) re-sorting implementation
│   ├── RedesignedSolution.java            # Continuous dual-index TreeSet + HashMap architecture
│   ├── ValidationBenchmark.java           # Comparative validation across read/write loads
│   ├── Output.txt                         # Empirical speedup evidence (up to 1,194x)
│   └── README.md                          # Sections A through F detailed engineering report
├── Assignment2_Report.md                  # Comprehensive Markdown Report for Submission
├── Assignment_CO2_Java_Completed.docx     # Formatted Word Document with Full Answers
└── README.md                              # Master Assignment Guide and Overview
```

---

## Steps to Compile and Run Each Task

### Prerequisites
- JDK 17 or higher installed (`javac -version`, `java -version`)
- Standard terminal (PowerShell, Bash, or Command Prompt)

### Task 01: String vs StringBuilder vs StringBuffer
```bash
javac Task01/TicketBenchmark.java
java -cp Task01 TicketBenchmark
```

### Task 02: ArrayList vs LinkedList vs ArrayDeque
```bash
javac Task02/PlaylistBenchmark.java
java -cp Task02 PlaylistBenchmark
```

### Task 03: hashCode-equals Contract Investigation
```bash
javac Task03/AttendanceBeforeFix.java Task03/Attendance.java Task03/HashCodeContractInvestigation.java

# 1. Run baseline without fix (demonstrates duplicates)
java -cp Task03 AttendanceBeforeFix

# 2. Run completed code with TODO 1 resolved
java -cp Task03 Attendance

# 3. Run extended contract investigation experiments
java -cp Task03 HashCodeContractInvestigation
```

### Task 04: Hospital Pharmacy Map Selection
```bash
javac Task04/PharmacyInventoryBenchmark.java
java -cp Task04 PharmacyInventoryBenchmark
```

### Task 05: Generic Leaderboard Component
```bash
javac Task05/LeaderboardDemo.java Task05/LeaderboardTest.java

# 1. Run completed assignment code (matches expected output)
java -cp Task05 LeaderboardDemo

# 2. Run extended edge case and generic reusability test
java -cp Task05 LeaderboardTest
```

### Task 06: Emergency Department Triage & PriorityQueue
```bash
javac Task06/Triage.java Task06/TriageBenchmarkAndFairness.java

# 1. Run completed triage code (matches expected output)
java -cp Task06 Triage

# 2. Run 8-patient verification, growth benchmark, and starvation prevention
java -cp Task06 TriageBenchmarkAndFairness
```

### Task 07: Day-4 Design Challenge (Diagnose, Redesign, Validate)
```bash
javac Task07/OriginalSolution.java Task07/RedesignedSolution.java Task07/ValidationBenchmark.java
java -cp Task07 ValidationBenchmark
```

---

## Progressive Git Commit History
```text
* b494400 - Task 07: Complete Day-4 Design Challenge - Diagnose, Redesign and Validate
* 924e9a8 - Task 06: Complete Hospital Emergency Triage and PriorityQueue investigation
* e222eb0 - Task 05: Complete Generic Leaderboard and Comparator ranking component
* b887970 - Task 04: Complete Hospital Pharmacy Inventory benchmark (HashMap vs TreeMap vs LinkedHashMap)
* 417e4f1 - Task 03: Complete hashCode-equals contract investigation and Attendance TODO 1
* 900a9a2 - Task 02: Complete Playlist benchmark (ArrayList vs LinkedList vs ArrayDeque)
* 266a504 - Task 01: Complete Railway e-ticket benchmark (String vs StringBuilder vs StringBuffer)
```
*(Demonstrates progressive, incremental development across all tasks)*

---

## Academic Integrity & Verification Statement
All source code, experimental benchmarks, empirical measurements, predictions, and analytical defenses presented in this repository were independently formulated, coded, executed, and evaluated by **K V Charan Teja (192424167)** in accordance with the Bloom's Taxonomy Level K4 (Analyse) and Level K5 (Evaluate) guidelines of Course Outcome 2 (CO2).
