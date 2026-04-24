package server;

import util.PasswordUtil;

import java.sql.*;

/**
 * Manages database connections and schema initialization.
 * Uses connection-per-request pattern for thread safety.
 */
public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_fee_db";
    private static final String DB_URL_NO_DB = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Get a connection to the student_fee_db database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /**
     * Initialize the database: create DB, tables, and seed default admin.
     */
    public static void initializeDatabase() {
        System.out.println("[DB] Initializing database...");

        // Create database if not exists
        try (Connection conn = DriverManager.getConnection(DB_URL_NO_DB, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS student_fee_db");
            System.out.println("[DB] Database 'student_fee_db' ensured.");
        } catch (SQLException e) {
            System.err.println("[DB] Error creating database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Create tables
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Users table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  username VARCHAR(50) UNIQUE NOT NULL," +
                "  password VARCHAR(128) NOT NULL," +
                "  role ENUM('ADMIN','STUDENT') NOT NULL," +
                "  status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING'" +
                ") ENGINE=InnoDB"
            );

            // Students table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS students (" +
                "  student_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  name VARCHAR(100) NOT NULL," +
                "  department VARCHAR(100) NOT NULL," +
                "  year INT NOT NULL," +
                "  total_fee DOUBLE NOT NULL DEFAULT 0," +
                "  user_id INT UNIQUE NOT NULL," +
                "  approval_date TIMESTAMP NULL," +
                "  FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ") ENGINE=InnoDB"
            );

            // Payments table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS payments (" +
                "  payment_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  student_id INT NOT NULL," +
                "  amount DOUBLE NOT NULL," +
                "  payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  status ENUM('PAID','PARTIAL','UNPAID') NOT NULL," +
                "  transaction_id VARCHAR(50) UNIQUE NOT NULL," +
                "  FOREIGN KEY (student_id) REFERENCES students(student_id)" +
                ") ENGINE=InnoDB"
            );

            System.out.println("[DB] Tables created or verified.");

            // Schema Migrations (Add columns if they don't exist)
            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING'");
                System.out.println("[DB] Added 'status' column to users table.");
            } catch (SQLException e) { /* Column likely already exists */ }

            try {
                stmt.executeUpdate("ALTER TABLE students ADD COLUMN approval_date TIMESTAMP NULL");
                System.out.println("[DB] Added 'approval_date' column to students table.");
            } catch (SQLException e) { /* Column likely already exists */ }

            // Ensure existing admins are APPROVED
            stmt.executeUpdate("UPDATE users SET status='APPROVED' WHERE role='ADMIN'");
            System.out.println("[DB] Verified admin statuses are 'APPROVED'.");

            System.out.println("[DB] Database initialization complete.");

            // Seed default admin if not exists
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM users WHERE username = 'admin'"
            );
            rs.next();
            if (rs.getInt(1) == 0) {
                String hashedPass = PasswordUtil.hash("admin123");
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, password, role, status) VALUES (?, ?, 'ADMIN', 'APPROVED')"
                );
                ps.setString(1, "admin");
                ps.setString(2, hashedPass);
                ps.executeUpdate();
                ps.close();
                System.out.println("[DB] Default admin account created (admin / admin123).");
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("[DB] Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[DB] Database initialization complete.");
    }
}
