package server;

import common.*;
import util.PasswordUtil;
import util.PDFGenerator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FeeServerImpl extends UnicastRemoteObject implements FeeService {
    private static final long serialVersionUID = 1L;

    public FeeServerImpl() throws RemoteException { super(); }

    @Override
    public User authenticate(String username, String password) throws RemoteException {
        System.out.println("[Server] Auth attempt: " + username);
        String hashed = PasswordUtil.hash(password);
        String sql = "SELECT user_id, username, role, status FROM users WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, hashed);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                String status = rs.getString("status");
                
                // Only students need to be APPROVED to log in
                if (!"ADMIN".equals(role)) {
                    if ("PENDING".equals(status)) {
                        throw new RemoteException("Account awaiting admin approval.");
                    } else if ("REJECTED".equals(status)) {
                        throw new RemoteException("Registration rejected by admin.");
                    }
                }
                
                User u = new User(rs.getInt("user_id"), rs.getString("username"), "", role, status);
                System.out.println("[Server] Auth OK: " + u);
                return u;
            }
        } catch (SQLException e) { throw new RemoteException("Auth failed: " + e.getMessage(), e); }
        System.out.println("[Server] Auth failed: " + username);
        return null;
    }

    @Override
    public String[] registerStudent(String name, String dept, int year, double totalFee) throws RemoteException {
        System.out.println("[Server] Admin Registering: " + name);
        String username = name.toLowerCase().replaceAll("\\s+", ".") + (1000 + new Random().nextInt(9000));
        String plainPass = genPass(8);
        String hashedPass = PasswordUtil.hash(plainPass);
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                PreparedStatement up = c.prepareStatement("INSERT INTO users (username,password,role,status) VALUES (?,?,'STUDENT','APPROVED')", Statement.RETURN_GENERATED_KEYS);
                up.setString(1, username); up.setString(2, hashedPass); up.executeUpdate();
                ResultSet k = up.getGeneratedKeys(); k.next(); int uid = k.getInt(1); k.close(); up.close();
                PreparedStatement sp = c.prepareStatement("INSERT INTO students (name,department,year,total_fee,user_id,approval_date) VALUES (?,?,?,?,?,CURRENT_TIMESTAMP)");
                sp.setString(1, name); sp.setString(2, dept); sp.setInt(3, year); sp.setDouble(4, totalFee); sp.setInt(5, uid);
                sp.executeUpdate(); sp.close();
                c.commit();
                System.out.println("[Server] Registered (Admin): " + username);
                return new String[]{username, plainPass};
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new RemoteException("Registration failed", e); }
    }

    @Override
    public boolean selfRegister(String name, String username, String password, String dept, int year) throws RemoteException {
        System.out.println("[Server] Self Registering: " + name + " (" + username + ")");
        String hashedPass = PasswordUtil.hash(password);
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                PreparedStatement up = c.prepareStatement("INSERT INTO users (username,password,role,status) VALUES (?,?,'STUDENT','PENDING')", Statement.RETURN_GENERATED_KEYS);
                up.setString(1, username); up.setString(2, hashedPass); up.executeUpdate();
                ResultSet k = up.getGeneratedKeys(); k.next(); int uid = k.getInt(1); k.close(); up.close();
                PreparedStatement sp = c.prepareStatement("INSERT INTO students (name,department,year,total_fee,user_id) VALUES (?,?,?,0,?)");
                sp.setString(1, name); sp.setString(2, dept); sp.setInt(3, year); sp.setInt(4, uid);
                sp.executeUpdate(); sp.close();
                c.commit();
                System.out.println("[Server] Self Registered (Pending): " + username);
                return true;
            } catch (SQLException e) { c.rollback(); if (e.getMessage().contains("Duplicate")) throw new RemoteException("Username already exists."); throw e; }
        } catch (SQLException e) { throw new RemoteException("Self-registration failed: " + e.getMessage(), e); }
    }

    @Override
    public List<Student> getPendingStudents() throws RemoteException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.username, u.status, 0 as paid_amount FROM students s JOIN users u ON s.user_id=u.user_id WHERE u.status='PENDING' ORDER BY s.student_id";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) { throw new RemoteException("Fetch pending failed", e); }
        return list;
    }

    @Override
    public boolean approveStudent(int studentId, double totalFee) throws RemoteException {
        System.out.println("[Server] Approving studentId=" + studentId + " with fee=" + totalFee);
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Get user_id
                int userId = -1;
                PreparedStatement ps = c.prepareStatement("SELECT user_id FROM students WHERE student_id=?");
                ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
                if (rs.next()) userId = rs.getInt(1); ps.close();
                
                if (userId == -1) return false;

                // Update user status
                PreparedStatement up = c.prepareStatement("UPDATE users SET status='APPROVED' WHERE user_id=?");
                up.setInt(1, userId); up.executeUpdate(); up.close();

                // Update student fee and approval date
                PreparedStatement sp = c.prepareStatement("UPDATE students SET total_fee=?, approval_date=CURRENT_TIMESTAMP WHERE student_id=?");
                sp.setDouble(1, totalFee); sp.setInt(2, studentId); sp.executeUpdate(); sp.close();

                c.commit();
                return true;
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new RemoteException("Approval failed", e); }
    }

    @Override
    public boolean rejectStudent(int studentId) throws RemoteException {
        System.out.println("[Server] Rejecting studentId=" + studentId);
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int userId = -1;
                PreparedStatement ps = c.prepareStatement("SELECT user_id FROM students WHERE student_id=?");
                ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
                if (rs.next()) userId = rs.getInt(1); ps.close();
                
                if (userId == -1) return false;

                PreparedStatement up = c.prepareStatement("UPDATE users SET status='REJECTED' WHERE user_id=?");
                up.setInt(1, userId); up.executeUpdate(); up.close();

                c.commit();
                return true;
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new RemoteException("Rejection failed", e); }
    }

    @Override
    public List<Student> getAllStudents() throws RemoteException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.username, u.status, COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.student_id=s.student_id),0) AS paid_amount " +
                     "FROM students s JOIN users u ON s.user_id=u.user_id " +
                     "WHERE u.role='STUDENT' AND u.status='APPROVED' ORDER BY s.student_id";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) { throw new RemoteException("Fetch students failed", e); }
        return list;
    }

    @Override
    public Student getStudentByUserId(int userId) throws RemoteException {
        String sql = "SELECT s.*, u.username, u.status, COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.student_id=s.student_id),0) AS paid_amount FROM students s JOIN users u ON s.user_id=u.user_id WHERE s.user_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapStudent(rs);
        } catch (SQLException e) { throw new RemoteException("Fetch student failed", e); }
        return null;
    }

    @Override
    public Payment makePayment(int studentId, double amount) throws RemoteException {
        System.out.println("[Server] Payment: sid=" + studentId + " amt=" + amount);
        if (amount <= 0) throw new RemoteException("Amount must be positive");
        double balance = getBalance(studentId);
        if (amount > balance) throw new RemoteException("Amount $" + String.format("%.2f", amount) + " exceeds balance $" + String.format("%.2f", balance));
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String status = (amount >= balance) ? "PAID" : "PARTIAL";
        String sql = "INSERT INTO payments (student_id,amount,status,transaction_id) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId); ps.setDouble(2, amount); ps.setString(3, status); ps.setString(4, txnId);
            ps.executeUpdate();
            ResultSet k = ps.getGeneratedKeys(); k.next(); int pid = k.getInt(1);
            String q2 = "SELECT p.*, s.name AS student_name FROM payments p JOIN students s ON p.student_id=s.student_id WHERE p.payment_id=?";
            PreparedStatement ps2 = c.prepareStatement(q2); ps2.setInt(1, pid);
            ResultSet rs = ps2.executeQuery();
            if (rs.next()) { Payment p = mapPayment(rs); System.out.println("[Server] Payment OK: " + txnId); return p; }
        } catch (SQLException e) { throw new RemoteException("Payment failed", e); }
        return null;
    }

    @Override
    public List<Payment> getPaymentsByStudentId(int studentId) throws RemoteException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name AS student_name FROM payments p JOIN students s ON p.student_id=s.student_id WHERE p.student_id=? ORDER BY p.payment_date DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapPayment(rs));
        } catch (SQLException e) { throw new RemoteException("Fetch payments failed", e); }
        return list;
    }

    @Override
    public List<Payment> getAllPayments() throws RemoteException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name AS student_name FROM payments p JOIN students s ON p.student_id=s.student_id ORDER BY p.payment_date DESC";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapPayment(rs));
        } catch (SQLException e) { throw new RemoteException("Fetch payments failed", e); }
        return list;
    }

    @Override
    public byte[] generateReceipt(int paymentId) throws RemoteException {
        System.out.println("[Server] Receipt for paymentId=" + paymentId);
        String sql = "SELECT p.*, s.name AS student_name FROM payments p JOIN students s ON p.student_id=s.student_id WHERE p.payment_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, paymentId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return PDFGenerator.generateReceipt(rs.getString("student_name"), rs.getInt("student_id"), rs.getDouble("amount"), rs.getTimestamp("payment_date").toString(), rs.getString("transaction_id"));
            else throw new RemoteException("Payment not found");
        } catch (SQLException e) { throw new RemoteException("Receipt failed", e);
        } catch (Exception e) { throw new RemoteException("PDF failed", e); }
    }

    private double getBalance(int studentId) throws RemoteException {
        String sql = "SELECT s.total_fee - COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.student_id=s.student_id),0) AS bal FROM students s WHERE s.student_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("bal");
        } catch (SQLException e) { throw new RemoteException("Balance failed", e); }
        return 0;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student(); s.setStudentId(rs.getInt("student_id")); s.setName(rs.getString("name"));
        s.setDepartment(rs.getString("department")); s.setYear(rs.getInt("year")); s.setTotalFee(rs.getDouble("total_fee"));
        s.setUserId(rs.getInt("user_id")); s.setUsername(rs.getString("username")); s.setPaidAmount(rs.getDouble("paid_amount"));
        s.setStatus(rs.getString("status")); s.setApprovalDate(rs.getTimestamp("approval_date"));
        return s;
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment(); p.setPaymentId(rs.getInt("payment_id")); p.setStudentId(rs.getInt("student_id"));
        p.setAmount(rs.getDouble("amount")); p.setPaymentDate(rs.getTimestamp("payment_date"));
        p.setStatus(rs.getString("status")); p.setTransactionId(rs.getString("transaction_id"));
        p.setStudentName(rs.getString("student_name")); return p;
    }

    private String genPass(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder(); Random r = new Random();
        for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
