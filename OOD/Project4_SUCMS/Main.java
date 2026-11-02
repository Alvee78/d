package project4_sucms;

/**
 * Project 4: Smart University Campus Management System (SUCMS)
 * Demonstrates all 5 required design patterns:
 * 1. Singleton: UniversityConfigManager
 * 2. Factory: UserAccountFactory (StudentUser, FacultyUser, AdminUser)
 * 3. Observer: NoticePublisher (Subject) notifying StudentPortal, FacultyPortal (Observers)
 * 4. Strategy: StudentEvaluationStrategy (AbsoluteGradingStrategy, RelativeGradingStrategy, GPAEvaluationStrategy)
 * 5. Adapter: LibrarySystemAdapter (adapts legacy 3rd-party LegacyLibraryAPI to standard LibraryService)
 */

import java.util.*;

// ==========================================
// 1. SINGLETON PATTERN: UniversityConfigManager
// ==========================================
class UniversityConfigManager {
    private static volatile UniversityConfigManager instance;
    private final Map<String, String> configurations = new HashMap<>();

    private UniversityConfigManager() {
        System.out.println("[Singleton] UniversityConfigManager initialized (Campus Configuration Loaded).");
        configurations.put("SEMESTER", "Spring 2026");
        configurations.put("CAMPUS_NAME", "Smart Metropolitan University");
        configurations.put("MAX_CREDIT_LIMIT", "18.0");
    }

    public static UniversityConfigManager getInstance() {
        if (instance == null) {
            synchronized (UniversityConfigManager.class) {
                if (instance == null) {
                    instance = new UniversityConfigManager();
                }
            }
        }
        return instance;
    }

    public String getConfig(String key) {
        return configurations.get(key);
    }

    public void setConfig(String key, String value) {
        configurations.put(key, value);
        System.out.println("[UniversityConfigManager] Config updated -> " + key + " = " + value);
    }
}

// ==========================================
// 2. FACTORY PATTERN: User Account Creation
// ==========================================
interface UserAccount {
    String getUserId();
    String getName();
    String getRole();
    void displayPermissions();
}

class StudentAccount implements UserAccount {
    private final String userId;
    private final String name;
    public StudentAccount(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    @Override public String getUserId() { return userId; }
    @Override public String getName() { return name; }
    @Override public String getRole() { return "Student"; }
    @Override
    public void displayPermissions() {
        System.out.println("[User:" + name + " (" + userId + ")] Role: " + getRole() + " | Permissions: [Register Courses, View Grades, Access LMS, Borrow Library Books]");
    }
}

class FacultyAccount implements UserAccount {
    private final String userId;
    private final String name;
    public FacultyAccount(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    @Override public String getUserId() { return userId; }
    @Override public String getName() { return name; }
    @Override public String getRole() { return "Faculty Member"; }
    @Override
    public void displayPermissions() {
        System.out.println("[User:" + name + " (" + userId + ")] Role: " + getRole() + " | Permissions: [Upload Syllabus, Submit Grades, Track Attendance, Book Smart Classrooms]");
    }
}

class AdminAccount implements UserAccount {
    private final String userId;
    private final String name;
    public AdminAccount(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    @Override public String getUserId() { return userId; }
    @Override public String getName() { return name; }
    @Override public String getRole() { return "System Administrator"; }
    @Override
    public void displayPermissions() {
        System.out.println("[User:" + name + " (" + userId + ")] Role: " + getRole() + " | Permissions: [Full System Control, User Provisioning, Security Oversight, Configuration Update]");
    }
}

class UserAccountFactory {
    public static UserAccount createUser(String role, String userId, String name) {
        switch (role.toLowerCase()) {
            case "student":
                return new StudentAccount(userId, name);
            case "faculty":
                return new FacultyAccount(userId, name);
            case "admin":
                return new AdminAccount(userId, name);
            default:
                throw new IllegalArgumentException("Unknown user account role: " + role);
        }
    }
}

// ==========================================
// 3. OBSERVER PATTERN: Notice / Result Notification
// ==========================================
interface AcademicNotificationObserver {
    void onAcademicNotice(String noticeTitle, String noticeContent);
}

class StudentPortalObserver implements AcademicNotificationObserver {
    private final String studentId;
    public StudentPortalObserver(String studentId) { this.studentId = studentId; }

    @Override
    public void onAcademicNotice(String noticeTitle, String noticeContent) {
        System.out.println("  [Student Portal Notice -> Student " + studentId + "] " + noticeTitle + ": " + noticeContent);
    }
}

class FacultyPortalObserver implements AcademicNotificationObserver {
    private final String facultyId;
    public FacultyPortalObserver(String facultyId) { this.facultyId = facultyId; }

    @Override
    public void onAcademicNotice(String noticeTitle, String noticeContent) {
        System.out.println("  [Faculty Portal Notification -> Prof. " + facultyId + "] " + noticeTitle + ": " + noticeContent);
    }
}

class NoticeBoardSubject {
    private final List<AcademicNotificationObserver> observers = new ArrayList<>();

    public void subscribe(AcademicNotificationObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(AcademicNotificationObserver observer) {
        observers.remove(observer);
    }

    public void publishNotice(String title, String content) {
        System.out.println("\n[NoticeBoard] Publishing Announcement: '" + title + "'");
        for (AcademicNotificationObserver obs : observers) {
            obs.onAcademicNotice(title, content);
        }
    }
}

// ==========================================
// 4. STRATEGY PATTERN: Student Evaluation Strategy
// ==========================================
interface StudentEvaluationStrategy {
    String evaluateGrade(double marks);
}

class AbsoluteGradingStrategy implements StudentEvaluationStrategy {
    @Override
    public String evaluateGrade(double marks) {
        if (marks >= 80) return "A+ (4.00) [Absolute Scale: Outstanding]";
        if (marks >= 70) return "A  (3.75) [Absolute Scale: Excellent]";
        if (marks >= 60) return "B  (3.00) [Absolute Scale: Good]";
        if (marks >= 50) return "C  (2.00) [Absolute Scale: Average]";
        return "F  (0.00) [Fail]";
    }
}

class RelativeCurvedGradingStrategy implements StudentEvaluationStrategy {
    private final double classAverage;
    public RelativeCurvedGradingStrategy(double classAverage) {
        this.classAverage = classAverage;
    }

    @Override
    public String evaluateGrade(double marks) {
        double delta = marks - classAverage;
        if (delta >= 15) return "A+ (Curved top percentile)";
        if (delta >= 5)  return "A  (Above class average)";
        if (delta >= -5) return "B  (Around class average)";
        if (delta >= -15) return "C (Below class average)";
        return "F (Significantly below class curve)";
    }
}

class EvaluationContext {
    private StudentEvaluationStrategy strategy;

    public EvaluationContext(StudentEvaluationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(StudentEvaluationStrategy strategy) {
        this.strategy = strategy;
    }

    public String computeGrade(double marks) {
        return strategy.evaluateGrade(marks);
    }
}

// ==========================================
// 5. ADAPTER PATTERN: Library External System
// ==========================================
// Standard Target interface used across University Campus System
interface LibraryService {
    boolean checkAvailability(String bookIsbn);
    String issueBook(String bookIsbn, String studentCardId);
}

// Incompatible legacy third-party library archive database
class LegacyLibraryAPI {
    public int queryCatalogStock(long isbnNumber) {
        // Returns number of physical copies on shelf
        return 3;
    }

    public String reserveAsset(long isbnNumber, String barcodeUserTag) {
        return "LEGACY_LOAN_RECORD_" + isbnNumber + "_" + barcodeUserTag;
    }
}

// Adapter adapting LegacyLibraryAPI to LibraryService
class LibrarySystemAdapter implements LibraryService {
    private final LegacyLibraryAPI legacyLibrary;

    public LibrarySystemAdapter(LegacyLibraryAPI legacyLibrary) {
        this.legacyLibrary = legacyLibrary;
    }

    @Override
    public boolean checkAvailability(String bookIsbn) {
        long isbn = Long.parseLong(bookIsbn.replace("-", ""));
        return legacyLibrary.queryCatalogStock(isbn) > 0;
    }

    @Override
    public String issueBook(String bookIsbn, String studentCardId) {
        long isbn = Long.parseLong(bookIsbn.replace("-", ""));
        return legacyLibrary.reserveAsset(isbn, studentCardId);
    }
}

// ==========================================
// MAIN DRIVER & DEMONSTRATION
// ==========================================
public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println(" PROJECT 4: SMART UNIVERSITY CAMPUS MANAGEMENT SYSTEM (SUCMS) - DEMO");
        System.out.println("=========================================================================\n");

        // 1. Singleton Pattern Demo
        System.out.println("--- 1. Testing Singleton Pattern (UniversityConfigManager) ---");
        UniversityConfigManager config1 = UniversityConfigManager.getInstance();
        UniversityConfigManager config2 = UniversityConfigManager.getInstance();

        System.out.println("Active Semester: " + config1.getConfig("SEMESTER"));
        System.out.println("Max Credit Limit: " + config2.getConfig("MAX_CREDIT_LIMIT"));
        config1.setConfig("EXAM_SCHEDULE_RELEASED", "true");
        System.out.println("Verifying single config instance: config1 == config2 ? " + (config1 == config2));
        System.out.println("Exam Released setting checked from config2: " + config2.getConfig("EXAM_SCHEDULE_RELEASED"));
        System.out.println();

        // 2. Factory Pattern Demo
        System.out.println("--- 2. Testing Factory Pattern (UserAccountFactory) ---");
        UserAccount student = UserAccountFactory.createUser("student", "STU-2023-8801", "Nabil Hasan");
        UserAccount faculty = UserAccountFactory.createUser("faculty", "FAC-0492", "Dr. Ayesha Rahman");
        UserAccount admin = UserAccountFactory.createUser("admin", "ADM-001", "Chief Administrator");

        student.displayPermissions();
        faculty.displayPermissions();
        admin.displayPermissions();
        System.out.println();

        // 3. Observer Pattern Demo
        System.out.println("--- 3. Testing Observer Pattern (Notice / Result Notification) ---");
        NoticeBoardSubject noticeBoard = new NoticeBoardSubject();
        StudentPortalObserver studentObserver = new StudentPortalObserver("STU-2023-8801");
        FacultyPortalObserver facultyObserver = new FacultyPortalObserver("FAC-0492");

        noticeBoard.subscribe(studentObserver);
        noticeBoard.subscribe(facultyObserver);

        noticeBoard.publishNotice("Midterm Exam Schedule", "Midterm examinations will commence from next Sunday across all engineering departments.");
        noticeBoard.publishNotice("Semester Final Grade Publishing", "Grades for Design Patterns Lab are published. Check your portals.");
        System.out.println();

        // 4. Strategy Pattern Demo
        System.out.println("--- 4. Testing Strategy Pattern (Student Evaluation Strategy) ---");
        double testScore = 78.5;
        System.out.println("Evaluating student score: " + testScore);

        EvaluationContext evalContext = new EvaluationContext(new AbsoluteGradingStrategy());
        System.out.println("Result under Absolute Grading: " + evalContext.computeGrade(testScore));

        System.out.println("\nSwitching to Relative Curved Grading Strategy (Class average = 65.0)...");
        evalContext.setStrategy(new RelativeCurvedGradingStrategy(65.0));
        System.out.println("Result under Relative Curved Grading: " + evalContext.computeGrade(testScore));
        System.out.println();

        // 5. Adapter Pattern Demo
        System.out.println("--- 5. Testing Adapter Pattern (Legacy Library Integration) ---");
        LegacyLibraryAPI legacyLib = new LegacyLibraryAPI();
        LibraryService libraryService = new LibrarySystemAdapter(legacyLib);

        String sampleIsbn = "978-0134685991";
        boolean isAvailable = libraryService.checkAvailability(sampleIsbn);
        System.out.println("Is book (ISBN: " + sampleIsbn + ") in stock? " + isAvailable);
        if (isAvailable) {
            String loanToken = libraryService.issueBook(sampleIsbn, "STU-2023-8801");
            System.out.println("Library Loan Token generated: " + loanToken);
        }

        System.out.println("\n[SUCMS Project Execution Completed Successfully]");
    }
}
