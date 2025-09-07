// Eclipse IDE - Timesheet & Payroll Application
// Author: Ezhilan
// Date: 2025-09-07

import java.util.*; // Import Java utilities like Map, HashMap, LinkedHashMap

// ---------------- Employee ----------------
class Employee {
    protected int empId;
    protected String name;
    protected String role;
    protected double hourlyRate;
    protected String bankAccount;

    // Constructor
    public Employee(int empId, String name, String role, double hourlyRate, String bankAccount) {
        this.empId = empId;
        this.name = name;
        this.role = role;
        this.hourlyRate = hourlyRate;
        this.bankAccount = bankAccount;
    }

    // Getters
    public int getEmpId() { return empId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public double getHourlyRate() { return hourlyRate; }
    public String getBankAccount() { return bankAccount; }

    // Setter
    public void setHourlyRate(double rate) { this.hourlyRate = rate; }

    // Compute basic pay
    public double computePay(double totalHours) {
        return totalHours * hourlyRate;
    }

    @Override
    public String toString() {
        return empId + " - " + name + " (" + role + ")";
    }
}

// ---------------- Manager ----------------
class Manager extends Employee {
    public Manager(int empId, String name, double hourlyRate, String bankAccount) {
        super(empId, name, "Manager", hourlyRate, bankAccount);
    }

    // Overriding computePay: +10% bonus
    @Override
    public double computePay(double totalHours) {
        return (totalHours * hourlyRate) * 1.10;
    }

    // Approve timesheet
    public void approveTimesheet(Timesheet sheet) {
        sheet.setState("APPROVED");
        System.out.println("Manager " + name + " approved timesheet: " + sheet.getSheetId());
    }
}

// ---------------- Project ----------------
class Project {
    private int projectId;
    private String name;
    private String client;
    private double billableRate;

    public Project(int projectId, String name, String client, double billableRate) {
        this.projectId = projectId;
        this.name = name;
        this.client = client;
        this.billableRate = billableRate;
    }

    public int getProjectId() { return projectId; }
    public String getName() { return name; }
    public String getClient() { return client; }
    public double getBillableRate() { return billableRate; }
}

// ---------------- Timesheet ----------------
class Timesheet {
    private int sheetId;
    private int empId;
    private Map<String, Double> entries; // date → hours
    private String weekStart;
    private String state; // DRAFT, SUBMITTED, APPROVED

    public Timesheet(int sheetId, int empId, String weekStart) {
        this.sheetId = sheetId;
        this.empId = empId;
        this.weekStart = weekStart;
        this.state = "DRAFT";
        this.entries = new LinkedHashMap<>(); // maintains insertion order
    }

    public int getSheetId() { return sheetId; }
    public int getEmpId() { return empId; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    // Log hours by date
    public void logHours(String date, double hours) {
        entries.put(date, entries.getOrDefault(date, 0.0) + hours);
    }

    // Log hours using start-end time
    public void logHours(String date, int startHour, int endHour) {
        double hours = endHour - startHour;
        entries.put(date, entries.getOrDefault(date, 0.0) + hours);
    }

    // Total hours
    public double getTotalHours() {
        double total = 0;
        for (double h : entries.values()) total += h;
        return total;
    }

    // Submit timesheet
    public void submit() {
        if (state.equals("DRAFT")) {
            state = "SUBMITTED";
            System.out.println("Timesheet " + sheetId + " submitted.");
        }
    }

    // Print all entries
    public void printEntries() {
        System.out.println("Timesheet " + sheetId + " Entries:");
        for (Map.Entry<String, Double> entry : entries.entrySet()) {
            System.out.println(" " + entry.getKey() + " → " + entry.getValue() + " hrs");
        }
    }
}

// ---------------- PayrollService ----------------
class PayrollService {
    private Map<Integer, Timesheet> timesheetDB = new HashMap<>();

    public void addTimesheet(Timesheet t) {
        timesheetDB.put(t.getSheetId(), t);
    }

    public void computePay(Employee e, Timesheet t) {
        if (!t.getState().equals("APPROVED")) {
            System.out.println("Timesheet not approved yet!");
            return;
        }
        double pay = e.computePay(t.getTotalHours());
        generateSlip(e, t, pay);
    }

    public void generateSlip(Employee e, Timesheet t, double pay) {
        System.out.println("\n---- PAYSLIP ----");
        System.out.println("Employee: " + e.getName());
        System.out.println("Role: " + e.getRole());
        System.out.println("Bank Account: " + e.getBankAccount());
        System.out.println("Timesheet ID: " + t.getSheetId());
        System.out.println("Total Hours: " + t.getTotalHours());
        System.out.println("Total Pay: ₹" + pay);
        System.out.println("-----------------\n");
    }
}

// ---------------- Main ----------------
public class TimePayrollMain {
    public static void main(String[] args) {
        // Create employees
        Employee emp1 = new Employee(101, "Ezhilan", "Developer", 500, "SBI12345");
        Manager mgr1 = new Manager(201, "Arjun", 800, "HDFC98765");

        // Create project
        Project proj1 = new Project(301, "Payroll System", "ABC Corp", 1200);

        // Create timesheet
        Timesheet t1 = new Timesheet(401, emp1.getEmpId(), "2025-09-01");

        // Log hours
        t1.logHours("2025-09-01", 8);
        t1.logHours("2025-09-02", 9);
        t1.logHours("2025-09-03", 10);
        t1.logHours("2025-09-04", 9, 18); // start–end format

        // Print timesheet entries
        t1.printEntries();

        // Submit timesheet
        t1.submit();

        // Manager approves
        mgr1.approveTimesheet(t1);

        // Payroll processing
        PayrollService payroll = new PayrollService();
        payroll.addTimesheet(t1);
        payroll.computePay(emp1, t1);
    }
}
