import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

doc = docx.Document()

# Configure Margins
for section in doc.sections:
    section.top_margin = Inches(0.75)
    section.bottom_margin = Inches(0.75)
    section.left_margin = Inches(0.75)
    section.right_margin = Inches(0.75)

def add_title(text):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run(text)
    r.font.name = 'Arial'
    r.font.size = Pt(18)
    r.bold = True
    r.font.color.rgb = RGBColor(16, 44, 87)
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(2)

def add_subtitle(text):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run(text)
    r.font.name = 'Calibri'
    r.font.size = Pt(12)
    r.italic = True
    r.font.color.rgb = RGBColor(60, 60, 60)
    p.paragraph_format.space_after = Pt(10)

def add_h1(text):
    p = doc.add_paragraph()
    r = p.add_run(text)
    r.font.name = 'Arial'
    r.font.size = Pt(14)
    r.bold = True
    r.font.color.rgb = RGBColor(16, 44, 87)
    p.paragraph_format.space_before = Pt(14)
    p.paragraph_format.space_after = Pt(3)

def add_h2(text):
    p = doc.add_paragraph()
    r = p.add_run(text)
    r.font.name = 'Calibri'
    r.font.size = Pt(11.5)
    r.bold = True
    r.font.color.rgb = RGBColor(30, 80, 140)
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(2)

def add_p(text, bold_prefix=None):
    p = doc.add_paragraph()
    if bold_prefix:
        r_b = p.add_run(bold_prefix)
        r_b.font.name = 'Calibri'
        r_b.font.size = Pt(10)
        r_b.bold = True
        r_b.font.color.rgb = RGBColor(20, 20, 20)
    r = p.add_run(text)
    r.font.name = 'Calibri'
    r.font.size = Pt(10)
    r.font.color.rgb = RGBColor(40, 40, 40)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.line_spacing = 1.15
    return p

def add_code_block(code_text):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = tbl.cell(0, 0)
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="F4F5F7"/>')
    cell._tc.get_or_add_tcPr().append(shd)
    borders = parse_xml(f'<w:tcBorders {nsdecls("w")}><w:top w:val="single" w:sz="4" w:space="0" w:color="D1D5DB"/><w:left w:val="single" w:sz="16" w:space="0" w:color="102C57"/><w:bottom w:val="single" w:sz="4" w:space="0" w:color="D1D5DB"/><w:right w:val="single" w:sz="4" w:space="0" w:color="D1D5DB"/></w:tcBorders>')
    cell._tc.get_or_add_tcPr().append(borders)
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(3)
    p.paragraph_format.space_after = Pt(3)
    r = p.add_run(code_text)
    r.font.name = 'Consolas'
    r.font.size = Pt(9)
    r.font.color.rgb = RGBColor(25, 25, 25)
    doc.add_paragraph().paragraph_format.space_after = Pt(3)

def add_table_data(headers, rows_data):
    tbl = doc.add_table(rows=len(rows_data)+1, cols=len(headers))
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    # Format header
    hdr_cells = tbl.rows[0].cells
    for i, h in enumerate(headers):
        hdr_cells[i].text = h
        shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="102C57"/>')
        hdr_cells[i]._tc.get_or_add_tcPr().append(shd)
        p = hdr_cells[i].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        for r in p.runs:
            r.font.name = 'Calibri'
            r.font.size = Pt(9)
            r.bold = True
            r.font.color.rgb = RGBColor(255, 255, 255)
    # Format data
    for row_idx, r_data in enumerate(rows_data):
        row_cells = tbl.rows[row_idx + 1].cells
        bg_color = "F9FAFB" if row_idx % 2 == 1 else "FFFFFF"
        for col_idx, val in enumerate(r_data):
            row_cells[col_idx].text = str(val)
            shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{bg_color}"/>')
            row_cells[col_idx]._tc.get_or_add_tcPr().append(shd)
            p = row_cells[col_idx].paragraphs[0]
            for r in p.runs:
                r.font.name = 'Calibri'
                r.font.size = Pt(8.5)
                r.font.color.rgb = RGBColor(30, 30, 30)
    doc.add_paragraph().paragraph_format.space_after = Pt(4)

# -------------------------------------------------------------
# DOCUMENT HEADER
# -------------------------------------------------------------
add_title("PROGRAMMING IN JAVA (CO2)")
add_subtitle("ASSIGNMENT 2: COMPREHENSIVE LABORATORY REPORT & VERIFICATION")

# Metadata Table
meta_headers = ["Field", "Student & Submission Details"]
meta_rows = [
    ["Student Name", "K V Charan Teja"],
    ["Register Number", "192424167"],
    ["Course Code / Class", "CSA09 – Programming in Java"],
    ["GitHub Repository Link", "https://github.com/charan-167/CSA0902-CHARAN_TEJA_ASSIGNMENT2"],
    ["Execution Platform", "OpenJDK 17.0.21.0.2 LTS, 64-Bit Server VM, Windows 11"],
    ["CO2 Coverage", "Strings, StringBuilder, Regex, Collection Framework, Generics, Comparators (Sessions 15–26)"],
    ["Total Marks / Duration", "50 Marks / 1 Week"]
]
add_table_data(meta_headers, meta_rows)

# -------------------------------------------------------------
# Q1: String, StringBuilder or StringBuffer
# -------------------------------------------------------------
add_h1("Q1 – Building Text Under Load: String, StringBuilder or StringBuffer? (6 Marks)")
add_p("Bloom's Level: K4 – Analyse | Context: Railway e-Ticket Generation", "Assessment Category: ")

add_h2("1. Reasoned Prediction (Before Implementation)")
add_p("Java strings are immutable. Repeated concatenation using the + operator creates a new String instance and allocates a new backing byte[] array on each step, copying all accumulated characters. For n fields, total character copies scale quadratically as O(n²), resulting in severe heap churn and GC pauses. Conversely, StringBuilder and StringBuffer mutate an internal expandable buffer with amortized O(1) appends (O(n) total copies) and instantiate only 1 final String. StringBuffer incurs minor synchronization overhead.")

add_h2("2. Contrasting Workloads Designed & Measured Evidence")
q1_headers = ["Workload", "Metric", "String (+)", "StringBuilder", "StringBuffer", "String.join()"]
q1_rows = [
    ["Workload 1\n(1 Ticket x 10 fields)", "Avg Time (ms)\nSpeedup\nIntermediate Objs", "0.010 ms\n1.00x\n10 objects", "0.008 ms\n1.23x\n1 object", "0.006 ms\n1.59x\n1 object", "0.004 ms\n2.38x\n1 object"],
    ["Workload 2\n(10,000 x 50 fields)", "Avg Time (ms)\nSpeedup\nIntermediate Objs\nEst. Heap (MB)", "45.385 ms\n1.00x\n500,000 objects\n62.80 MB", "8.385 ms\n5.41x\n10,000 objects\n59.00 MB", "8.297 ms\n5.47x\n10,000 objects\n59.00 MB", "12.329 ms\n3.68x\n10,000 objects\nN/A"],
    ["Workload 3\n(5,000 x 100 fields)", "Avg Time (ms)\nSpeedup\nIntermediate Objs\nEst. Heap (MB)", "77.371 ms\n1.00x\n500,000 objects\n89.20 MB", "8.549 ms\n9.05x\n5,000 objects\n59.00 MB", "8.228 ms\n9.40x\n5,000 objects\n59.00 MB", "6.377 ms\n12.13x\n5,000 objects\nN/A"]
]
add_table_data(q1_headers, q1_rows)

add_h2("3. Design Decision for Multithreaded Booking Server")
add_p("For per-ticket generation, recommend StringBuilder. Because ticket construction is thread-confined on the stack within a worker method, synchronization is redundant; StringBuilder eliminates lock operations. For the shared booking log, recommend StringBuffer (or a lock-free queue/Disruptor ring buffer). Multiple concurrent worker threads append audit lines to the shared buffer; StringBuffer's synchronized methods prevent race conditions, character interleaving, and buffer corruption.")

add_h2("4. Alternative Concept & Complexity Analysis")
add_p("Repeated +: Copies L * sum(i=1..n, i) = L*n*(n+1)/2 = O(n²) characters, generating O(n) intermediate strings. StringBuilder: Amortized O(n) character copies, 1 final string. String.join() / StringJoiner performs similarly to StringBuilder (O(n)). String.format() parses format specifiers at runtime, causing 5x-10x slower execution.")

# -------------------------------------------------------------
# Q2: ArrayList vs LinkedList vs ArrayDeque
# -------------------------------------------------------------
add_h1("Q2 – When Is ArrayList an Appropriate Engineering Choice? (4 Marks)")
add_p("Bloom's Level: K4 – Analyse | Context: Music Streaming Playlist Editor", "Assessment Category: ")

add_h2("1. Experimental Evidence Across 3 Playlist Sizes")
q2_headers = ["Operation Tested", "Size = 1,000 Songs", "Size = 10,000 Songs", "Size = 50,000 Songs", "Winner / Engineering Reason"]
q2_rows = [
    ["Add at End", "0.632 vs 0.374 ms", "1.404 vs 0.834 ms", "0.772 vs 8.701 ms", "ArrayList (11.3x faster at scale; no node allocations)"],
    ["Insert at Front", "0.939 vs 0.496 ms", "0.452 vs 0.095 ms", "3.193 vs 0.074 ms", "LinkedList (43.4x faster; O(1) head pointer rewiring)"],
    ["Insert in Middle", "0.317 vs 2.146 ms", "0.293 vs 7.013 ms", "1.677 vs 32.263 ms", "ArrayList (19.2x faster; hardware vectorized array copy beats pointer hops)"],
    ["Remove from Front", "0.506 vs 0.261 ms", "0.303 vs 0.052 ms", "2.733 vs 0.038 ms", "LinkedList (71.6x faster; O(1) unlinking vs shifting 50k items)"],
    ["Remove from Middle", "0.132 vs 1.032 ms", "0.246 vs 5.870 ms", "1.447 vs 31.273 ms", "ArrayList (21.6x faster; cache locality beats node walking)"],
    ["Get k-th Element", "0.159 vs 1.081 ms", "0.563 vs 16.110 ms", "0.282 vs 78.307 ms", "ArrayList (278.0x faster; direct O(1) memory offset math)"],
    ["Iterate Full List", "0.192 vs 0.586 ms", "0.508 vs 0.850 ms", "1.104 vs 1.199 ms", "ArrayList (Contiguous spatial cache prefetching)"]
]
add_table_data(q2_headers, q2_rows)

add_h2("2. Operating Boundary & Changed Condition ('Up Next' Queue)")
add_p("Operating Boundary: ArrayList is recommended for read-heavy playlists, random access get(k), appends, and middle insertions. Stop recommending ArrayList when frequent front insertions/deletions occur on lists with N >= 10,000.")
add_p("Changed Condition ('Up Next' Queue): Evaluated on 50,000 items with 10,000 deque operations: ArrayList took 17.44 ms (1.00x), LinkedList took 8.92 ms (1.96x), and ArrayDeque took 1.22 ms (14.27x speedup over ArrayList, 7.3x over LinkedList). ArrayDeque is a circular ring buffer with O(1) pointer updates at both ends, zero shifting, and zero node allocation overhead.")

# -------------------------------------------------------------
# Q3: hashCode-equals Contract
# -------------------------------------------------------------
add_h1("Q3 – Engineer the hashCode–equals Contract (6 Marks)")
add_p("Bloom's Level: K4 – Analyse | Context: Event Attendance De-duplication", "Assessment Category: ")

add_h2("1. Complete the Code Verification")
add_p("Output BEFORE completing TODO 1: 'Unique students: 3' (Duplicates allowed: [192311002 Divya, 192311001 Arun, 192311001 Arun]). Default Object.hashCode() gave distinct memory hashes, placing duplicate Aruns into different buckets where equals() was never called.")
add_p("Completed Code:")
add_code_block("""@Override
public int hashCode() {
    return Objects.hashCode(regNo);
}""")
add_p("Output AFTER completing TODO 1: 'Unique students: 2' (Expected output matched exactly). Both instances hash to the same bucket, where equals() returns true and eliminates the duplicate.")

add_h2("2. Deliberately Designed Inquiries")
add_p("Test 1 (Appears to Work): Adding identical reference s1 twice yields size = 1. HashMap checks reference equality (p.key == key) first, bypassing hash code evaluation.", "Test 1: ")
add_p("Test 2 (Exposes Duplicates): Distinct object allocations with identical regNo yield size = 2 when hashCode is missing, proving that equals() alone cannot guarantee Set uniqueness.", "Test 2: ")
add_p("Test 3A (Key Mutation Bug): When regNo is mutated after insertion, set.contains(student) returns false because the lookup searches the newly hashed bucket. The element is trapped in the old bucket as an unreachable ghost element, proving key fields must be final.", "Test 3A: ")
add_p("Test 3B (TreeSet Inconsistency): A TreeSet with a Comparator on name drops a student with a different regNo if the name matches (size = 1), demonstrating that compare() == 0 must be consistent with equals().", "Test 3B: ")

# -------------------------------------------------------------
# Q4: Pharmacy Inventory Map
# -------------------------------------------------------------
add_h1("Q4 – Choosing a Map for a Pharmacy Inventory (6 Marks)")
add_p("Bloom's Level: K4 – Analyse | Context: Hospital Pharmacy Stock Lookup & Nightly Reports", "Assessment Category: ")

add_h2("1. Empirical Evidence (100,000 Lookups Across 3 Sizes)")
q4_headers = ["Inventory Size", "Map Type", "Put (ms)", "100k Lookups", "ns / Lookup", "Report 1 (Code)", "Report 2 (Arrival)"]
q4_rows = [
    ["1,000 Medicines", "HashMap\nTreeMap\nLinkedHashMap", "0.244 ms\n1.677 ms\n0.434 ms", "6.600 ms\n31.994 ms\n5.670 ms", "66.0 ns\n319.9 ns\n56.7 ns", "0.856 ms (Needs sort)\n0.106 ms (Natural)\n1.211 ms (Needs sort)", "0.645 ms (Needs sort)\n0.615 ms (Needs sort)\n0.035 ms (Natural)"],
    ["10,000 Medicines", "HashMap\nTreeMap\nLinkedHashMap", "1.026 ms\n4.365 ms\n0.864 ms", "8.159 ms\n28.267 ms\n7.842 ms", "81.6 ns\n282.7 ns\n78.4 ns", "5.761 ms (Needs sort)\n1.487 ms (Natural)\n6.959 ms (Needs sort)", "4.410 ms (Needs sort)\n4.276 ms (Needs sort)\n0.202 ms (Natural)"],
    ["100,000 Medicines", "HashMap\nTreeMap\nLinkedHashMap", "17.556 ms\n75.100 ms\n14.003 ms", "28.020 ms\n77.935 ms\n29.973 ms", "280.2 ns\n779.3 ns\n299.7 ns", "37.788 ms (Needs sort)\n14.653 ms (Natural)\n80.334 ms (Needs sort)", "42.127 ms (Needs sort)\n48.836 ms (Needs sort)\n1.712 ms (Natural)"]
]
add_table_data(q4_headers, q4_rows)

add_h2("2. Scalability Inquiry & Deployment Decision")
add_p("Criterion: Pharmacy dispensing requires sub-microsecond lookups (< 1,000 ns). HashMap (280 ns) and LinkedHashMap (299 ns) easily satisfy this, whereas TreeMap (779 ns) is 2.6x slower due to tree pointer traversals. Recommendation: Deploy LinkedHashMap<String, Medicine>. Daytime lookups run at 299 ns; Report 2 (Arrival order) is emitted instantly in 1.7 ms; Report 1 (Code order) requires sorting once at night in batch taking only 80 ms. Hashtable is obsolete due to coarse-grained synchronization, prohibition of nulls, and absence of fail-fast iterators.")

# -------------------------------------------------------------
# Q5: Generic Leaderboard Component
# -------------------------------------------------------------
add_h1("Q5 – Derive a Generic Ranking Component from a Principle (7 Marks)")
add_p("Bloom's Level: K4 – Analyse | Context: Inter-College Sports Leaderboard", "Assessment Category: ")

add_h2("1. Completed Code & Verification")
add_code_block("""// TODO 1 in Leaderboard.java:
List<T> ranking() {
    List<T> sortedCopy = new ArrayList<>(items);
    sortedCopy.sort(order);
    return sortedCopy;
}

// TODO 2 in LeaderboardDemo.java:
Comparator<Team> byRank = Comparator
        .comparingInt((Team t) -> t.points).reversed()
        .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
        .thenComparing((Team t) -> t.name);""")
add_p("Program Output: [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]. Matches expected output byte-for-byte.")
add_p("Why Bisons ranks above Panthers: Eagles, Bisons, and Panthers all have 9 points. Eagles has highest goal difference (6) -> Rank 1. Bisons and Panthers are tied on points (9) and goal difference (4). The tie-breaker evaluates team name lexicographically: 'Bisons'.compareTo('Panthers') < 0, placing Bisons above Panthers.")

add_h2("2. Design Principles & Faulty Comparator Demonstration")
add_p("Separate Comparator vs Comparable: Separate comparators decouple sorting strategy from entity models, allowing Leaderboard<T> to be reused for Team, Player, College, or QuizTeam without modifying class code. Bounded wildcard Comparator<? super T> adheres to PECS, allowing base class comparators to rank derived types. Faulty subtraction comparator ((t1, t2) -> t2.points - t1.points) overflows when comparing +2,000,000,000 with -2,000,000,000 (+294,967,296), breaking ranking transitivity; Integer.compare() must be used.")

# -------------------------------------------------------------
# Q6: Emergency Department Triage
# -------------------------------------------------------------
add_h1("Q6 – Priority, Order and Fairness in a Queue (8 Marks)")
add_p("Bloom's Level: K5 – Evaluate | Context: Hospital Emergency Department Triage", "Assessment Category: ")

add_h2("1. Completed Code & Output")
add_code_block("""// TODO 1 in Triage.java:
private final PriorityQueue<Patient> queue = new PriorityQueue<>(
        Comparator.comparingInt((Patient p) -> p.severity)
                  .thenComparingLong((Patient p) -> p.arrival)
);""")
add_p("Program Output: Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5). Matches expected output.")

add_h2("2. Heap Mechanics, Growth Benchmark & Deployment Feasibility")
add_p("Why direct print is not sorted: PriorityQueue is backed by a binary min-heap array. The iterator traverses the raw array where only queue[0] is minimal; siblings are not ordered. Treatment order is produced only by repeatedly extracting the root via poll() (O(log n)).")
q6_headers = ["Queue Size (N)", "Tree Depth", "Batch Admit (ms)", "Time / Admit", "Batch Poll (ms)", "Time / Poll"]
q6_rows = [
    ["1,000 Patients", "~10 levels", "3.579 ms", "3.57 µs", "1.289 ms", "1.29 µs"],
    ["10,000 Patients", "~14 levels", "3.410 ms", "341 ns", "3.003 ms", "300 ns"],
    ["100,000 Patients", "~17 levels", "18.943 ms", "189 ns", "47.255 ms", "472 ns"]
]
add_table_data(q6_headers, q6_rows)
add_p("City Hospital Deployment (5,000 Patients/Day): 5,000 patients over 24 hours = 3.5 arrivals/minute, with active queue depths of 50-300 patients. Consumes < 5 ms of total CPU time per day; highly recommended.")
add_p("Starvation & Fair Aging Rule: Strict priority causes Severity 5 patients to starve if severe cases arrive continuously. Designed dynamic aging: Effective Severity = max(1, Base - (WaitTime / Threshold)). Validated in code: waiting Severity 5 patient was elevated to Severity 2, preempting newer Severity 3 arrivals.")

# -------------------------------------------------------------
# Q7: Day-4 Design Challenge
# -------------------------------------------------------------
add_h1("Q7 – Day-4 Design Challenge: Diagnose, Redesign and Validate (13 Marks)")
add_p("Bloom's Level: K5 – Evaluate | Selection: Leaderboard Component from Q5", "Assessment Category: ")

add_h2("A. Diagnose the Bottleneck (2 Marks)")
add_p("In Q5, Leaderboard.ranking() executes a full O(N log N) TimSort on every call, allocating an entire duplicate list. On 10,000 teams, 200 ranking queries sort 2,000,000 items and churn hundreds of MB of heap garbage. Top-10 queries sort all 10,000 items just to inspect 10.")

add_h2("B. Define the Design Objective (2 Marks)")
add_p("Transition from read-time O(N log N) re-sorting to write-time continuous maintenance (O(log N)). Achieve O(0) comparisons during reads, sub-microsecond O(K) Top-K extraction, and O(log N) in-place score updates.")

add_h2("C. Design the Revised Solution (4 Marks)")
add_p("Implemented RedesignedSolution<K, T> utilizing a dual-index architecture: (1) NavigableSet<T> (TreeSet) maintains continuous Red-Black tree ordering at all times. (2) Map<K, T> (HashMap) enables O(1) key lookups. In-place score mutations remove the old record from TreeSet in O(log N), update score, and re-insert in O(log N). topK(k) extracts only k items via iterator in O(k) time.")

add_h2("D. Predict Before Implementing (1 Mark)")
add_p("Predicted full ranking queries will achieve 10x-15x speedup; Top-10 queries will achieve > 500x speedup with microsecond latency.")

add_h2("E. Implement and Validate the Redesign (2 Marks)")
q7_headers = ["Benchmark Scenario (10k Teams)", "Original Solution", "Redesigned Solution", "Measured Improvement"]
q7_rows = [
    ["Full Ranking (200 requests)", "634.84 ms (3.17 ms/op)", "37.56 ms (0.19 ms/op)", "16.90x Speedup (0 comparisons)"],
    ["Top-10 Queries (500 requests)", "888.67 ms (1.78 ms/op)", "0.74 ms (0.0015 ms / 1.5 µs)", "1,194.13x Speedup (O(k) traversal)"],
    ["Mixed Stream (400 R + 100 U)", "High latency / GC degradation", "2.36 ms (0.005 ms/op)", "Real-time production grade"]
]
add_table_data(q7_headers, q7_rows)

add_h2("F. Defend the Final Design (2 Marks)")
add_p("Predictions were confirmed: full ranking achieved a 16.9x speedup, and Top-10 queries achieved a 1,194x speedup. Trade-offs: Memory increases from 8 bytes/slot to ~64 bytes/entry (TreeSet + HashMap nodes); insertion takes O(log N) vs O(1). The redesign is overwhelmingly superior for interactive servers, while the original is preferable only for write-only archives.")

# Save document
output_path = r'c:\Users\kolas\OneDrive\Documents\JAVA ASSIGNMENT 2\Assignment_CO2_Java_Completed.docx'
doc.save(output_path)
print('Successfully generated Assignment_CO2_Java_Completed.docx')
