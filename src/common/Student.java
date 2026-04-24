package common;

import java.io.Serializable;

/**
 * Represents a student with fee information.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int studentId;
    private String name;
    private String department;
    private int year;
    private double totalFee;
    private int userId;
    private String username;
    private double paidAmount;
    private String status;       // PENDING, APPROVED, REJECTED
    private java.sql.Timestamp approvalDate;

    public Student() {}

    public Student(int studentId, String name, String department, int year,
                   double totalFee, int userId) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.totalFee = totalFee;
        this.userId = userId;
    }

    public double getBalance() {
        return totalFee - paidAmount;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.sql.Timestamp getApprovalDate() { return approvalDate; }
    public void setApprovalDate(java.sql.Timestamp approvalDate) { this.approvalDate = approvalDate; }

    @Override
    public String toString() {
        return "Student{id=" + studentId + ", name='" + name + "', dept='" + department +
               "', year=" + year + ", totalFee=" + totalFee + ", paid=" + paidAmount + "}";
    }
}
