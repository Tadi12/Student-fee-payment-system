package client;

import common.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Student Dashboard with sidebar navigation, profile, payment, and history panels.
 */
public class StudentDashboard extends JFrame {

    private final FeeService service;
    private final User currentUser;
    private Student student;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private static final Color SIDEBAR_BG = new Color(18, 20, 32);
    private static final Color CONTENT_BG = new Color(24, 27, 42);
    private static final Color CARD_BG = new Color(32, 36, 56);
    private static final Color CARD_BORDER = new Color(50, 55, 80);
    private static final Color PRIMARY = new Color(67, 97, 238);
    private static final Color TEXT_PRIMARY = new Color(230, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(140, 145, 170);
    private static final Color SUCCESS = new Color(6, 214, 160);
    private static final Color DANGER = new Color(239, 71, 111);
    private static final Color WARNING = new Color(255, 209, 102);
    private static final Color NAV_HOVER = new Color(35, 40, 65);
    private static final Color NAV_ACTIVE = new Color(67, 97, 238, 40);

    private JButton activeNavButton = null;
    private JLabel profileName, profileDept, profileYear, profileFee, profilePaid, profileBalance;
    private DefaultTableModel historyTableModel;
    private PaymentForm paymentForm;

    public StudentDashboard(FeeService service, User user) {
        this.service = service;
        this.currentUser = user;
        loadStudentData();
        initUI();
    }

    private void loadStudentData() {
        try {
            student = service.getStudentByUserId(currentUser.getUserId());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load student data: " + e.getMessage());
        }
    }

    private void initUI() {
        setTitle("Student Dashboard - " + (student != null ? student.getName() : ""));
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);

        mainPanel.add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);

        contentPanel.add(createProfilePanel(), "profile");
        paymentForm = new PaymentForm(service, student, () -> {
            refreshProfile();
            refreshHistory();
        });
        contentPanel.add(paymentForm, "pay");
        contentPanel.add(createHistoryPanel(), "history");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

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

        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        sep.setMaximumSize(new Dimension(230, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(12));

        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(TEXT_SECONDARY);
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 24, 8, 0));
        sidebar.add(navLabel);

        JButton profileBtn = createNavButton("\u263A  My Profile", "profile");
        JButton payBtn = createNavButton("\uD83D\uDCB3  Pay Fees", "pay");
        JButton historyBtn = createNavButton("\u25A4  History", "history");

        sidebar.add(profileBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(payBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(historyBtn);

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

        JLabel userLabel = new JLabel(student != null ? student.getName() : currentUser.getUsername());
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
        logoutBtn.addActionListener(e -> { dispose(); new LoginForm(service).setVisible(true); });
        userPanel.add(logoutBtn);
        sidebar.add(userPanel);

        setActiveNav(profileBtn);
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
        btn.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn != activeNavButton) btn.setBackground(NAV_HOVER); }
            public void mouseExited(MouseEvent e) { if (btn != activeNavButton) btn.setBackground(SIDEBAR_BG); }
        });
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            setActiveNav(btn);
            if ("profile".equals(cardName)) refreshProfile();
            if ("history".equals(cardName)) refreshHistory();
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

    // ── PROFILE PANEL ──
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel header = new JLabel("My Profile");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);
        panel.add(header, BorderLayout.NORTH);

        JPanel card = createRoundedCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(500, 320));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.WEST;

        profileName = new JLabel();
        profileDept = new JLabel();
        profileYear = new JLabel();
        profileFee = new JLabel();
        profilePaid = new JLabel();
        profileBalance = new JLabel();

        int row = 0;
        addProfileRow(card, gbc, row++, "Student ID:", student != null ? String.valueOf(student.getStudentId()) : "-");
        addProfileRow(card, gbc, row++, "Name:", profileName);
        addProfileRow(card, gbc, row++, "Department:", profileDept);
        addProfileRow(card, gbc, row++, "Year:", profileYear);
        addProfileRow(card, gbc, row++, "Total Fee:", profileFee);
        addProfileRow(card, gbc, row++, "Paid Amount:", profilePaid);
        addProfileRow(card, gbc, row++, "Balance:", profileBalance);

        refreshProfile();

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        wrapper.add(card);
        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private void addProfileRow(JPanel card, GridBagConstraints gbc, int row, String label, Object value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_SECONDARY);
        card.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        if (value instanceof JLabel) {
            JLabel val = (JLabel) value;
            val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            val.setForeground(TEXT_PRIMARY);
            card.add(val, gbc);
        } else {
            JLabel val = new JLabel(value.toString());
            val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            val.setForeground(TEXT_PRIMARY);
            card.add(val, gbc);
        }
    }

    private void refreshProfile() {
        new SwingWorker<Student, Void>() {
            protected Student doInBackground() throws Exception {
                return service.getStudentByUserId(currentUser.getUserId());
            }
            protected void done() {
                try {
                    student = get();
                    if (student != null) {
                        profileName.setText(student.getName());
                        profileDept.setText(student.getDepartment());
                        profileYear.setText(String.valueOf(student.getYear()));
                        profileFee.setText("$" + String.format("%.2f", student.getTotalFee()));
                        profilePaid.setText("$" + String.format("%.2f", student.getPaidAmount()));
                        profileBalance.setText("$" + String.format("%.2f", student.getBalance()));
                        profileBalance.setForeground(student.getBalance() > 0 ? DANGER : SUCCESS);
                    }
                } catch (Exception e) { /* ignore */ }
            }
        }.execute();
    }

    // ── HISTORY PANEL ──
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel header = new JLabel("Payment History");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);

        JButton downloadBtn = new JButton("Download Receipt");
        downloadBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setBackground(PRIMARY);
        downloadBtn.setFocusPainted(false);
        downloadBtn.setBorderPainted(false);
        downloadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadBtn.setPreferredSize(new Dimension(160, 36));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(header, BorderLayout.WEST);
        topBar.add(downloadBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Payment ID", "Amount", "Date", "Status", "Transaction ID"};
        historyTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scrollPane.getViewport().setBackground(CONTENT_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        downloadBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a payment to download receipt.");
                return;
            }
            int paymentId = (int) historyTableModel.getValueAt(selectedRow, 0);
            downloadReceipt(paymentId);
        });

        refreshHistory();
        return panel;
    }

    private void refreshHistory() {
        if (student == null) return;
        new SwingWorker<List<Payment>, Void>() {
            protected List<Payment> doInBackground() throws Exception {
                return service.getPaymentsByStudentId(student.getStudentId());
            }
            protected void done() {
                try {
                    List<Payment> payments = get();
                    historyTableModel.setRowCount(0);
                    for (Payment p : payments) {
                        historyTableModel.addRow(new Object[]{
                            p.getPaymentId(),
                            String.format("$%.2f", p.getAmount()),
                            p.getPaymentDate().toString(),
                            p.getStatus(),
                            p.getTransactionId()
                        });
                    }
                } catch (Exception e) { /* ignore */ }
            }
        }.execute();
    }

    private void downloadReceipt(int paymentId) {
        new SwingWorker<byte[], Void>() {
            protected byte[] doInBackground() throws Exception {
                return service.generateReceipt(paymentId);
            }
            protected void done() {
                try {
                    byte[] pdf = get();
                    File dir = new File("receipts");
                    if (!dir.exists()) dir.mkdirs();
                    File file = new File(dir, "receipt_" + paymentId + ".pdf");
                    try (FileOutputStream fos = new FileOutputStream(file)) { fos.write(pdf); }
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                        "Receipt saved to: " + file.getAbsolutePath(),
                        "Receipt Downloaded", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                        "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
