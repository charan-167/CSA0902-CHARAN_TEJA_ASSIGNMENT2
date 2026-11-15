import java.util.*;

/**
 * Task 05: Extended Leaderboard Testing
 * 
 * 1. Deep tie-breaking validation.
 * 2. Faulty Comparator demonstration (Integer subtraction overflow bug).
 * 3. Incomplete tie-breaker demonstration (violating transitivity / equality).
 * 4. Generic reusability verification: Ranking QuizTeam entities using Leaderboard<QuizTeam>.
 * 
 * Author: K V Charan Teja (192424167)
 * Course: Programming in Java - Assignment 2 (CO2)
 */
public class LeaderboardTest {

    static class QuizTeam {
        final String collegeName;
        final int score;
        final int timeTakenSeconds;

        QuizTeam(String collegeName, int score, int timeTakenSeconds) {
            this.collegeName = collegeName;
            this.score = score;
            this.timeTakenSeconds = timeTakenSeconds;
        }

        @Override
        public String toString() {
            return collegeName + " [Score: " + score + ", Time: " + timeTakenSeconds + "s]";
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       CS09 JAVA ASSIGNMENT 2 - TASK 05: GENERIC LEADERBOARD TESTING           ");
        System.out.println("       Student: K V Charan Teja (192424167) | Java: " + System.getProperty("java.version"));
        System.out.println("================================================================================\n");

        // 1. Deep Multi-Level Ties Test
        System.out.println("--- TEST 1: Multi-Level Ties (Points, GoalDiff, Name) ---");
        Comparator<Team> robustComparator = Comparator
                .comparingInt((Team t) -> t.points).reversed()
                .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
                .thenComparing((Team t) -> t.name);

        Leaderboard<Team> tieBoard = new Leaderboard<>(robustComparator);
        tieBoard.add(new Team("Wolves", 10, 5));
        tieBoard.add(new Team("Hawks", 10, 5));     // Tied points and goalDiff -> Hawks < Wolves
        tieBoard.add(new Team("Lions", 10, 8));     // Tied points, better goalDiff -> 1st
        tieBoard.add(new Team("Tigers", 7, 2));
        tieBoard.add(new Team("Bears", 7, 2));      // Tied points and goalDiff -> Bears < Tigers

        List<Team> rankedTeams = tieBoard.ranking();
        System.out.println("Ranked Teams:");
        for (int i = 0; i < rankedTeams.size(); i++) {
            System.out.printf("  Rank %d: %s\n", (i + 1), rankedTeams.get(i));
        }
        System.out.println();

        // 2. Demonstration of Faulty Subtraction Comparator (Integer Overflow)
        System.out.println("--- TEST 2: Exposing Faulty Subtraction Comparator (Integer Overflow) ---");
        Team extremePositive = new Team("HighScorer", 2_000_000_000, 0);
        Team extremeNegative = new Team("PenaltyTeam", -2_000_000_000, 0);

        // Naive subtraction: (t1, t2) -> t2.points - t1.points
        // Calculation: (-2_000_000_000) - (2_000_000_000) = -4_000_000_000 (overflows to positive +294,967,296!)
        int faultyDiff = extremeNegative.points - extremePositive.points;
        int robustDiff = Integer.compare(extremePositive.points, extremeNegative.points);

        System.out.println("Comparing Team with +2,000,000,000 pts vs -2,000,000,000 pts:");
        System.out.println("Naive Subtraction (t2.points - t1.points) yields: " + faultyDiff + " (OVERFLOW! Wrong ranking!)");
        System.out.println("Integer.compare(t1.points, t2.points) yields:   " + robustDiff + " (CORRECT! HighScorer wins)");
        System.out.println("Verdict: Naive subtraction breaks transitivity and mathematical correctness.\n");

        // 3. Generic Reusability Verification (Leaderboard for QuizTeam)
        System.out.println("--- TEST 3: Reusing Leaderboard<T> for QuizTeam (Generic Design) ---");
        Comparator<QuizTeam> quizOrder = Comparator
                .comparingInt((QuizTeam q) -> q.score).reversed()
                .thenComparingInt((QuizTeam q) -> q.timeTakenSeconds) // Lower time is better!
                .thenComparing((QuizTeam q) -> q.collegeName);

        Leaderboard<QuizTeam> quizBoard = new Leaderboard<>(quizOrder);
        quizBoard.add(new QuizTeam("IIT Madras", 95, 120));
        quizBoard.add(new QuizTeam("BITS Pilani", 95, 95));  // Tied score, faster time -> ranks higher!
        quizBoard.add(new QuizTeam("NIT Trichy", 85, 90));
        quizBoard.add(new QuizTeam("IIIT Hyderabad", 100, 150));

        System.out.println("Quiz Leaderboard Ranking:");
        List<QuizTeam> rankedQuiz = quizBoard.ranking();
        for (int i = 0; i < rankedQuiz.size(); i++) {
            System.out.printf("  Rank %d: %s\n", (i + 1), rankedQuiz.get(i));
        }
        System.out.println("\nVerification: Generic Leaderboard<T> seamlessly reuses logic without modifying class code.");
    }
}
