package client;

import common.FeeService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Registration form with professional branding and glassmorphism design.
 */
public class RegistrationForm extends JFrame {

    private final FeeService service;
    private final JFrame loginForm;
    
    private JTextField nameField, usernameField, deptField, yearField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel statusLabel;

    // Colors
    private static final Color PRIMARY = new Color(67, 97, 238);
    private static final Color TEXT_PRIMARY = new Color(230, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(140, 145, 170);
    private static final Color FIELD_BG = new Color(20, 23, 36);
    private static final Color CARD_BORDER = new Color(55, 60, 90);
    private static final Color ERROR = new Color(239, 71, 111);

    public RegistrationForm(FeeService service, JFrame loginForm) {
        this.service = service;
        this.loginForm = loginForm;
        initUI();
    }

    private void initUI() {
        setTitle("Student Registration - Professional Portal");
        setSize(520, 820);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with ambient glow
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 12, 20), 
                                                   getWidth(), getHeight(), new Color(25, 30, 50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.setColor(new Color(67, 97, 238, 12));
                g2.fillOval(-100, 200, 400, 400);
                g2.dispose();
            }
        };

        // Glassmorphism Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 40, 65, 230));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 32, 32));
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(255, 255, 255, 30));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 32, 32));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        card.setPreferredSize(new Dimension(440, 720));

        // Logo section
        JPanel logoSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        logoSection.setOpaque(false);
        JLabel logoIcon = new JLabel("\uD83C\uDF93");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        logoIcon.setForeground(PRIMARY);
        JLabel logoText = new JLabel("STUDENT FEES");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setForeground(TEXT_PRIMARY);
        logoSection.add(logoIcon);
        logoSection.add(logoText);
        logoSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logoSection);
        card.add(Box.createVerticalStrut(25));

        // Title
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        
        JLabel subtitle = new JLabel("Join the professional payment portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));

        // Form Fields with horizontal layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 15);

        addInputRow(formPanel, gbc, 0, "FULL NAME:", nameField = new JTextField());
        addInputRow(formPanel, gbc, 1, "USERNAME:", usernameField = new JTextField());
        addInputRow(formPanel, gbc, 2, "PASSWORD:", passwordField = new JPasswordField());
        addInputRow(formPanel, gbc, 3, "DEPARTMENT:", deptField = new JTextField());
        addInputRow(formPanel, gbc, 4, "YEAR (1-6):", yearField = new JTextField());
        
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(formPanel);
        card.add(Box.createVerticalStrut(30));

        // Register button
        registerButton = new JButton("Complete Registration");
        stylePrimaryButton(registerButton);
        registerButton.addActionListener(e -> performRegistration());
        card.add(registerButton);
        card.add(Box.createVerticalStrut(15));

        // Back link
        JButton backBtn = new JButton("Return to Login");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backBtn.setForeground(TEXT_SECONDARY);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            loginForm.setVisible(true);
        });
        card.add(backBtn);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);

        mainPanel.add(card);
        setContentPane(mainPanel);
        
        // Handle window events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginForm.setVisible(true);
            }
            @Override
            public void windowOpened(WindowEvent e) {
                nameField.requestFocusInWindow();
            }
        });
    }

    private void addInputRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridy = row;
        
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_SECONDARY);
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        styleField(field);
        panel.add(field, gbc);
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(86, 115, 255)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY); }
        });
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(FIELD_BG);
        f.setCaretColor(TEXT_PRIMARY);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void performRegistration() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String dept = deptField.getText().trim();
        String yearStr = yearField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || dept.isEmpty() || yearStr.isEmpty()) {
            statusLabel.setText("All fields are required");
            statusLabel.setForeground(ERROR);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
            if (year < 1 || year > 6) throw new Exception();
        } catch (Exception e) {
            statusLabel.setText("Invalid year (1-6)");
            statusLabel.setForeground(ERROR);
            return;
        }

        registerButton.setEnabled(false);
        statusLabel.setText("Submitting...");
        statusLabel.setForeground(TEXT_SECONDARY);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return service.selfRegister(name, username, password, dept, year);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(RegistrationForm.this, 
                            "Registration successful!\nYour account is now pending admin approval.", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        loginForm.setVisible(true);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setForeground(ERROR);
                } finally {
                    registerButton.setEnabled(true);
                }
            }
        }.execute();
    }
}
