package common;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a fee payment transaction.
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int paymentId;
    private int studentId;
    private double amount;
    private Timestamp paymentDate;
    private String status;       // PAID, PARTIAL, UNPAID
    private String transactionId;
    private String studentName;  // populated when fetching

    public Payment() {}

    public Payment(int paymentId, int studentId, double amount,
                   Timestamp paymentDate, String status, String transactionId) {
        this.paymentId = paymentId;
        this.studentId = studentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.transactionId = transactionId;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    @Override
    public String toString() {
        return "Payment{id=" + paymentId + ", studentId=" + studentId +
               ", amount=" + amount + ", status='" + status + "', txn='" + transactionId + "'}";
    }
}
