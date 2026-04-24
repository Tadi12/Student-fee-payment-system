package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI Remote interface for the Fee Payment Service.
 * Defines all operations available to clients.
 */
public interface FeeService extends Remote {

    /**
     * Authenticate a user by username and password.
     * @return User object on success, null on failure
     */
    User authenticate(String username, String password) throws RemoteException;

    /**
     * Register a new student (Admin only).
     * @return String array {username, password} of the generated credentials
     */
    String[] registerStudent(String name, String department, int year, double totalFee)
            throws RemoteException;

    /**
     * Self-register a student. Status will be PENDING.
     */
    boolean selfRegister(String name, String username, String password, String dept, int year)
            throws RemoteException;

    /**
     * Get students with PENDING status.
     */
    List<Student> getPendingStudents() throws RemoteException;

    /**
     * Approve a student and assign their fee.
     */
    boolean approveStudent(int studentId, double totalFee) throws RemoteException;

    /**
     * Reject a student registration.
     */
    boolean rejectStudent(int studentId) throws RemoteException;

    /**
     * Get all registered students.
     */
    List<Student> getAllStudents() throws RemoteException;

    /**
     * Get a student by their associated user ID.
     */
    Student getStudentByUserId(int userId) throws RemoteException;

    /**
     * Make a fee payment for a student.
     * @return the Payment record created
     */
    Payment makePayment(int studentId, double amount) throws RemoteException;

    /**
     * Get payment history for a specific student.
     */
    List<Payment> getPaymentsByStudentId(int studentId) throws RemoteException;

    /**
     * Get all payments across all students (Admin only).
     */
    List<Payment> getAllPayments() throws RemoteException;

    /**
     * Generate a PDF receipt for a payment.
     * @return PDF file as byte array
     */
    byte[] generateReceipt(int paymentId) throws RemoteException;
}
