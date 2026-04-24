package client;

import common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Payment form panel embedded in StudentDashboard.
 * Handles fee payment with balance display and receipt download.
 */
public class PaymentForm extends JPanel {

    private final FeeService service;
    private final Student student;
    private JLabel balanceLabel;
    private JTextField amountField;
    private JButton payButton;
    private JLabel statusLabel;
    private Runnable onPaymentComplete;

    private static final Color CARD_BG = new Color(32, 36, 56);
    private static final Color CARD_BORDER = new Color(50, 55, 80);
    private static final Color PRIMARY = new Color(67, 97, 238);
    private static final Color TEXT_PRIMARY = new Color(230, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(140, 145, 170);
    private static final Color SUCCESS = new Color(6, 214, 160);
    private static final Color DANGER = new Color(239, 71, 111);
    private static final Color FIELD_BG = new Color(20, 23, 36);
    private static final Color CONTENT_BG = new Color(24, 27, 42);

    public PaymentForm(FeeService service, Student student, Runnable onPaymentComplete) {
        this.service = service;
        this.student = student;
        this.onPaymentComplete = onPaymentComplete;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel header = new JLabel("Make Payment");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        // Center card
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        card.setMaximumSize(new Dimension(500, 400));
        card.setPreferredSize(new Dimension(460, 360));

        // Balance display
        JLabel balTitle = new JLabel("Outstanding Balance");
        balTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        balTitle.setForeground(TEXT_SECONDARY);
        balTitle.setAlignmentX(LEFT_ALIGNMENT);
        card.add(balTitle);
        card.add(Box.createVerticalStrut(4));

        balanceLabel = new JLabel("$" + String.format("%.2f", student.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        balanceLabel.setForeground(student.getBalance() > 0 ? DANGER : SUCCESS);
        balanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(balanceLabel);
        card.add(Box.createVerticalStrut(8));

        JLabel feeInfo = new JLabel("Total Fee: $" + String.format("%.2f", student.getTotalFee()) +
                                     "  |  Paid: $" + String.format("%.2f", student.getPaidAmount()));
        feeInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        feeInfo.setForeground(TEXT_SECONDARY);
        feeInfo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(feeInfo);
        card.add(Box.createVerticalStrut(28));

        // Amount input
        JLabel amtLabel = new JLabel("PAYMENT AMOUNT ($)");
        amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        amtLabel.setForeground(TEXT_SECONDARY);
        amtLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(amtLabel);
        card.add(Box.createVerticalStrut(6));

        amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        amountField.setForeground(TEXT_PRIMARY);
        amountField.setBackground(FIELD_BG);
        amountField.setCaretColor(TEXT_PRIMARY);
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        amountField.setAlignmentX(LEFT_ALIGNMENT);
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        card.add(amountField);
        card.add(Box.createVerticalStrut(20));

        // Pay button
        payButton = new JButton("Pay Now");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(SUCCESS);
        payButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        payButton.setAlignmentX(LEFT_ALIGNMENT);
        payButton.setFocusPainted(false);
        payButton.setBorderPainted(false);
        payButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        payButton.addActionListener(e -> processPayment());
        card.add(payButton);
        card.add(Box.createVerticalStrut(12));

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(statusLabel);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private void processPayment() {
        String amtText = amountField.getText().trim();
        if (amtText.isEmpty()) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter an amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtText);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Enter a valid positive amount");
            return;
        }

        payButton.setEnabled(false);
        payButton.setText("Processing...");
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setText("Processing payment...");

        new SwingWorker<Payment, Void>() {
            protected Payment doInBackground() throws Exception {
                return service.makePayment(student.getStudentId(), amount);
            }
            protected void done() {
                try {
                    Payment payment = get();
                    statusLabel.setForeground(SUCCESS);
                    statusLabel.setText("Payment successful! Transaction: " + payment.getTransactionId());
                    amountField.setText("");

                    // Update balance display
                    student.setPaidAmount(student.getPaidAmount() + amount);
                    balanceLabel.setText("$" + String.format("%.2f", student.getBalance()));
                    balanceLabel.setForeground(student.getBalance() > 0 ? DANGER : SUCCESS);

                    if (onPaymentComplete != null) onPaymentComplete.run();

                    // Auto-download receipt
                    int choice = JOptionPane.showConfirmDialog(PaymentForm.this,
                        "Payment of $" + String.format("%.2f", amount) + " successful!\n" +
                        "Transaction ID: " + payment.getTransactionId() + "\n\n" +
                        "Download receipt PDF?", "Payment Successful",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) downloadReceipt(payment.getPaymentId());

                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    statusLabel.setForeground(DANGER);
                    statusLabel.setText("Error: " + msg);
                } finally {
                    payButton.setEnabled(true);
                    payButton.setText("Pay Now");
                }
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
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(pdf);
                    }
                    JOptionPane.showMessageDialog(PaymentForm.this,
                        "Receipt saved to: " + file.getAbsolutePath(),
                        "Receipt Downloaded", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PaymentForm.this,
                        "Failed to download receipt: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void refreshBalance() {
        balanceLabel.setText("$" + String.format("%.2f", student.getBalance()));
        balanceLabel.setForeground(student.getBalance() > 0 ? DANGER : SUCCESS);
    }
}
