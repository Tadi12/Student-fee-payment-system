package client;

import common.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Admin Dashboard with sidebar navigation and card-based panels.
 */
public class AdminDashboard extends JFrame {

    private final FeeService service;
    private final User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Colors
    private static final Color SIDEBAR_BG = new Color(18, 20, 32);
    private static final Color CONTENT_BG = new Color(24, 27, 42);
    private static final Color CARD_BG = new Color(32, 36, 56);
    private static final Color CARD_BORDER = new Color(50, 55, 80);
    private static final Color PRIMARY = new Color(67, 97, 238);
    private static final Color TEXT_PRIMARY = new Color(230, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(140, 145, 170);
    private static final Color SUCCESS = new Color(6, 214, 160);
    private static final Color WARNING = new Color(255, 209, 102);
    private static final Color DANGER = new Color(239, 71, 111);
    private static final Color FIELD_BG = new Color(20, 23, 36);
    private static final Color NAV_HOVER = new Color(35, 40, 65);
    private static final Color NAV_ACTIVE = new Color(67, 97, 238, 40);

    private JButton activeNavButton = null;

    public AdminDashboard(FeeService service, User user) {
        this.service = service;
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Admin Dashboard - Student Fee Payment System");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);

        contentPanel.add(createOverviewPanel(), "overview");
        contentPanel.add(createApprovalsPanel(), "approvals");
        contentPanel.add(createRegisterPanel(), "register");
        contentPanel.add(createStudentsPanel(), "students");
        contentPanel.add(createPaymentsPanel(), "payments");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // ── SIDEBAR ──
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, CARD_BORDER));

        // Logo area with professional branding
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(230, 70));
        logoPanel.setPreferredSize(new Dimension(230, 70));
        
        JLabel logoIcon = new JLabel("\uD83C\uDF93");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        logoIcon.setForeground(PRIMARY);
        
        JLabel logoText = new JLabel("STUDENT FEES");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoText.setForeground(TEXT_PRIMARY);
        
        logoPanel.add(logoIcon);
        logoPanel.add(logoText);
        sidebar.add(logoPanel);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        sep.setMaximumSize(new Dimension(230, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(12));

        // Nav label with proper left padding
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(TEXT_SECONDARY);
        navLabel.setAlignmentX(LEFT_ALIGNMENT);
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 24, 8, 0));
        sidebar.add(navLabel);

        // Nav buttons with standard compatible symbols
        JButton overviewBtn = createNavButton("\u25FC  Overview", "overview");
        JButton approvalsBtn = createNavButton("\u2713  Approvals", "approvals");
        JButton registerBtn = createNavButton("\u271A  Register Student", "register");
        JButton studentsBtn = createNavButton("\u263A  All Students", "students");
        JButton paymentsBtn = createNavButton("\u0024  Payments", "payments");

        sidebar.add(overviewBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(approvalsBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(registerBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(studentsBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(paymentsBtn);

        sidebar.add(Box.createVerticalGlue());

        // User info + logout
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 16, 16));
        userPanel.setMaximumSize(new Dimension(230, 90));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(CARD_BORDER);
        sep2.setMaximumSize(new Dimension(200, 1));
        userPanel.add(sep2);
        userPanel.add(Box.createVerticalStrut(10));

        JLabel userLabel = new JLabel("Admin: " + currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(TEXT_SECONDARY);
        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(6));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setForeground(DANGER);
        logoutBtn.setBackground(SIDEBAR_BG);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm(service).setVisible(true);
        });
        userPanel.add(logoutBtn);
        sidebar.add(userPanel);

        // Set initial active
        setActiveNav(overviewBtn);
        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setPreferredSize(new Dimension(230, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Strict left padding for professional alignment
        btn.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn != activeNavButton) btn.setBackground(NAV_HOVER); }
            public void mouseExited(MouseEvent e) { if (btn != activeNavButton) btn.setBackground(SIDEBAR_BG); }
        });
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            setActiveNav(btn);
            if ("students".equals(cardName)) refreshStudentsTable();
            if ("payments".equals(cardName)) refreshPaymentsTable();
            if ("overview".equals(cardName)) refreshOverview();
            if ("approvals".equals(cardName)) refreshApprovalsTable();
        });
        return btn;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavButton != null) {
            activeNavButton.setBackground(SIDEBAR_BG);
            activeNavButton.setForeground(TEXT_SECONDARY);
        }
        activeNavButton = btn;
        btn.setBackground(NAV_ACTIVE);
        btn.setForeground(PRIMARY);
    }

    // ── OVERVIEW PANEL ──
    private JLabel totalStudentsLabel, totalRevenueLabel, pendingLabel;

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header
        JLabel header = new JLabel("Dashboard Overview");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);
        panel.add(header, BorderLayout.NORTH);

        // Stats cards
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 18, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        totalStudentsLabel = new JLabel("0");
        totalRevenueLabel = new JLabel("$0.00");
        pendingLabel = new JLabel("0");

        statsRow.add(createStatCard("Total Students", totalStudentsLabel, "\uD83D\uDC65", PRIMARY));
        statsRow.add(createStatCard("Total Revenue", totalRevenueLabel, "\uD83D\uDCB0", SUCCESS));
        statsRow.add(createStatCard("Pending Fees", pendingLabel, "\u23F3", WARNING));

        panel.add(statsRow, BorderLayout.CENTER);
        refreshOverview();
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(CARD_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                // Accent bar at top
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 4, 4, 4));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(0, 140));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        card.add(iconLbl);
        card.add(Box.createVerticalStrut(10));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_SECONDARY);
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_PRIMARY);
        card.add(valueLabel);

        return card;
    }

    private void refreshOverview() {
        new SwingWorker<Void, Void>() {
            List<Student> students; List<Payment> payments; List<Student> pending;
            protected Void doInBackground() throws Exception {
                students = service.getAllStudents(); 
                payments = service.getAllPayments(); 
                pending = service.getPendingStudents();
                return null;
            }
            protected void done() {
                try { get();
                    totalStudentsLabel.setText(String.valueOf(students.size()));
                    double rev = payments.stream().mapToDouble(Payment::getAmount).sum();
                    totalRevenueLabel.setText("$" + String.format("%.2f", rev));
                    pendingLabel.setText(String.valueOf(pending.size()));
                } catch (Exception e) { /* ignore */ }
            }
        }.execute();
    }

    // ── REGISTER STUDENT PANEL ──
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel header = new JLabel("Register New Student");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);
        panel.add(header, BorderLayout.NORTH);

        // Form card
        JPanel card = createRoundedCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField nameField = createStyledField();
        JTextField deptField = createStyledField();
        JTextField yearField = createStyledField();
        JTextField feeField = createStyledField();
        JLabel resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultLabel.setForeground(SUCCESS);

        int row = 0;
        addFormRow(card, gbc, row++, "Full Name:", nameField);
        addFormRow(card, gbc, row++, "Department:", deptField);
        addFormRow(card, gbc, row++, "Year:", yearField);
        addFormRow(card, gbc, row++, "Total Fee ($):", feeField);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton registerBtn = createAccentButton("Register Student");
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            String yearStr = yearField.getText().trim();
            String feeStr = feeField.getText().trim();

            if (name.isEmpty() || dept.isEmpty() || yearStr.isEmpty() || feeStr.isEmpty()) {
                resultLabel.setForeground(DANGER);
                resultLabel.setText("All fields are required");
                return;
            }
            try {
                int yr = Integer.parseInt(yearStr);
                double fee = Double.parseDouble(feeStr);
                registerBtn.setEnabled(false);
                new SwingWorker<String[], Void>() {
                    protected String[] doInBackground() throws Exception {
                        return service.registerStudent(name, dept, yr, fee);
                    }
                    protected void done() {
                        try {
                            String[] creds = get();
                            resultLabel.setForeground(SUCCESS);
                            resultLabel.setText("Registered! Username: " + creds[0] + "  |  Password: " + creds[1]);
                            nameField.setText(""); deptField.setText(""); yearField.setText(""); feeField.setText("");
                            JOptionPane.showMessageDialog(AdminDashboard.this,
                                "Student registered successfully!\n\nUsername: " + creds[0] + "\nPassword: " + creds[1] +
                                "\n\nPlease share these credentials with the student.",
                                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            resultLabel.setForeground(DANGER);
                            resultLabel.setText("Error: " + ex.getMessage());
                        } finally { registerBtn.setEnabled(true); }
                    }
                }.execute();
            } catch (NumberFormatException ex) {
                resultLabel.setForeground(DANGER);
                resultLabel.setText("Year must be integer, Fee must be number");
            }
        });
        card.add(registerBtn, gbc);

        gbc.gridy = row + 1;
        card.add(resultLabel, gbc);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        wrapper.add(card);
        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    // ── APPROVALS PANEL ──
    private DefaultTableModel approvalsTableModel;
    private JTable approvalsTable;

    private JPanel createApprovalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel header = new JLabel("Pending Student Registrations");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(header, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton refreshBtn = createAccentButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> refreshApprovalsTable());
        
        JButton approveBtn = new JButton("Approve Selected");
        styleTableButton(approveBtn, SUCCESS);
        approveBtn.addActionListener(e -> handleApproval());

        JButton rejectBtn = new JButton("Reject Selected");
        styleTableButton(rejectBtn, DANGER);
        rejectBtn.addActionListener(e -> handleRejection());

        btnPanel.add(refreshBtn);
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        topBar.add(btnPanel, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Name", "Username", "Dept", "Year", "Status"};
        approvalsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        approvalsTable = createStyledTable(approvalsTableModel);
        JScrollPane scrollPane = new JScrollPane(approvalsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scrollPane.getViewport().setBackground(CONTENT_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTableButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
    }

    private void refreshApprovalsTable() {
        new SwingWorker<List<Student>, Void>() {
            protected List<Student> doInBackground() throws Exception { return service.getPendingStudents(); }
            protected void done() {
                try {
                    List<Student> students = get();
                    approvalsTableModel.setRowCount(0);
                    for (Student s : students) {
                        approvalsTableModel.addRow(new Object[]{
                            s.getStudentId(), s.getName(), s.getUsername(), s.getDepartment(),
                            s.getYear(), s.getStatus()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Error: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void handleApproval() {
        int row = approvalsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student to approve");
            return;
        }
        int studentId = (int) approvalsTableModel.getValueAt(row, 0);
        String name = (String) approvalsTableModel.getValueAt(row, 1);

        String feeStr = JOptionPane.showInputDialog(this, "Enter Total Fee for " + name + ":", "Assign Fee", JOptionPane.QUESTION_MESSAGE);
        if (feeStr == null || feeStr.trim().isEmpty()) return;

        try {
            double fee = Double.parseDouble(feeStr);
            new SwingWorker<Boolean, Void>() {
                protected Boolean doInBackground() throws Exception { return service.approveStudent(studentId, fee); }
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(AdminDashboard.this, "Student Approved!");
                            refreshApprovalsTable();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Error: " + ex.getMessage());
                    }
                }
            }.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid fee amount");
        }
    }

    private void handleRejection() {
        int row = approvalsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student to reject");
            return;
        }
        int studentId = (int) approvalsTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reject this registration?", "Confirm Reject", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            protected Boolean doInBackground() throws Exception { return service.rejectStudent(studentId); }
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Student Rejected");
                        refreshApprovalsTable();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── STUDENTS TABLE PANEL ──
    private DefaultTableModel studentsTableModel;

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel header = new JLabel("All Registered Students");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);

        JButton refreshBtn = createAccentButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> refreshStudentsTable());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(header, BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Username", "Department", "Year", "Total Fee", "Paid", "Balance", "Status"};
        studentsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(studentsTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scrollPane.getViewport().setBackground(CONTENT_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshStudentsTable() {
        new SwingWorker<List<Student>, Void>() {
            protected List<Student> doInBackground() throws Exception { return service.getAllStudents(); }
            protected void done() {
                try {
                    List<Student> students = get();
                    studentsTableModel.setRowCount(0);
                    for (Student s : students) {
                        String status = s.getBalance() <= 0 ? "PAID" : "PENDING";
                        studentsTableModel.addRow(new Object[]{
                            s.getStudentId(), s.getName(), s.getUsername(), s.getDepartment(),
                            s.getYear(), String.format("$%.2f", s.getTotalFee()),
                            String.format("$%.2f", s.getPaidAmount()),
                            String.format("$%.2f", s.getBalance()), status
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Error: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ── PAYMENTS TABLE PANEL ──
    private DefaultTableModel paymentsTableModel;

    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel header = new JLabel("Payment History");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);

        JButton refreshBtn = createAccentButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> refreshPaymentsTable());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(header, BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Payment ID", "Student", "Amount", "Date", "Status", "Transaction ID"};
        paymentsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(paymentsTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scrollPane.getViewport().setBackground(CONTENT_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshPaymentsTable() {
        new SwingWorker<List<Payment>, Void>() {
            protected List<Payment> doInBackground() throws Exception { return service.getAllPayments(); }
            protected void done() {
                try {
                    List<Payment> payments = get();
                    paymentsTableModel.setRowCount(0);
                    for (Payment p : payments) {
                        paymentsTableModel.addRow(new Object[]{
                            p.getPaymentId(), p.getStudentName(), String.format("$%.2f", p.getAmount()),
                            p.getPaymentDate().toString(), p.getStatus(), p.getTransactionId()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Error: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ── HELPERS ──
    private JPanel createRoundedCard() {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(CARD_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        return card;
    }

    private void addFormRow(JPanel card, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_SECONDARY);
        card.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        field.setPreferredSize(new Dimension(300, 38));
        card.add(field, gbc);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(FIELD_BG);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER), BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return f;
    }

    private JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 42));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(86, 115, 255)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY); }
        });
        return btn;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(CARD_BG);
        table.setGridColor(CARD_BORDER);
        table.setRowHeight(36);
        table.setSelectionBackground(new Color(67, 97, 238, 60));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setForeground(TEXT_SECONDARY);
        th.setBackground(SIDEBAR_BG);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER));

        return table;
    }
}
