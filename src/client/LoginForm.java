package client;

import common.FeeService;
import common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern login screen with professional branding and glassmorphism design.
 */
public class LoginForm extends JFrame {

    private final FeeService service;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    // Colors
    private static final Color PRIMARY = new Color(67, 97, 238);
    private static final Color PRIMARY_HOVER = new Color(86, 115, 255);
    private static final Color TEXT_PRIMARY = new Color(230, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(140, 145, 170);
    private static final Color FIELD_BG = new Color(20, 23, 36);
    private static final Color CARD_BORDER = new Color(55, 60, 90);

    public LoginForm(FeeService service) {
        this.service = service;
        initUI();
    }

    private void initUI() {
        setTitle("Student Fee System - Professional Portal");
        setSize(520, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

                g2.setColor(new Color(67, 97, 238, 15));
                g2.fillOval(-100, -100, 400, 400);
                g2.setColor(new Color(114, 9, 183, 10));
                g2.fillOval(getWidth() - 250, getHeight() - 300, 400, 400);
                
                g2.dispose();
            }
        };

        // Glassmorphism Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 40, 65, 220));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 32, 32));
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(255, 255, 255, 30));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 32, 32));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(45, 50, 45, 50));
        card.setPreferredSize(new Dimension(420, 550));

        // Brand Logo
        JPanel logoSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        logoSection.setOpaque(false);
        JLabel logoIcon = new JLabel("\uD83C\uDF93");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        logoIcon.setForeground(PRIMARY);
        JLabel logoText = new JLabel("STUDENT FEES");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(TEXT_PRIMARY);
        logoSection.add(logoIcon);
        logoSection.add(logoText);
        logoSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logoSection);
        card.add(Box.createVerticalStrut(35));

        // Welcome Text
        JLabel title = new JLabel("Professional Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("Access your account and payments");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(40));

        // Inputs with GridBagLayout for horizontal alignment
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 15);

        addInputRow(formPanel, gbc, 0, "USERNAME:", usernameField = new JTextField(15));
        addInputRow(formPanel, gbc, 1, "PASSWORD:", passwordField = new JPasswordField(15));
        
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(formPanel);
        card.add(Box.createVerticalStrut(25));

        // Login button
        loginButton = new JButton("Login to Dashboard");
        stylePrimaryButton(loginButton);
        loginButton.addActionListener(e -> performLogin());
        card.add(loginButton);
        card.add(Box.createVerticalStrut(20));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(15));

        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        JLabel noAccount = new JLabel("New here?");
        noAccount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAccount.setForeground(TEXT_SECONDARY);
        JButton registerBtn = new JButton("Create an Account");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerBtn.setForeground(PRIMARY);
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> {
            new RegistrationForm(service, this).setVisible(true);
            setVisible(false);
        });
        registerPanel.add(noAccount);
        registerPanel.add(registerBtn);
        card.add(registerPanel);

        mainPanel.add(card);
        setContentPane(mainPanel);

        // Listeners
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) { usernameField.requestFocusInWindow(); }
        });
    }

    private void addInputRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridy = row;
        
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_SECONDARY);
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        styleTextField(field);
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
            public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_HOVER); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY); }
        });
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(FIELD_BG);
        field.setCaretColor(TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
    }

    private void performLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please enter credentials");
            statusLabel.setForeground(new Color(239, 71, 111));
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(TEXT_SECONDARY);

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return service.authenticate(user, pass);
            }

            @Override
            protected void done() {
                try {
                    User u = get();
                    if (u != null) {
                        dispose();
                        if ("ADMIN".equals(u.getRole())) {
                            new AdminDashboard(service, u).setVisible(true);
                        } else {
                            new StudentDashboard(service, u).setVisible(true);
                        }
                    }
                } catch (Exception e) {
                    statusLabel.setText("Connection error: " + e.getMessage());
                    statusLabel.setForeground(new Color(239, 71, 111));
                } finally {
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }
}
