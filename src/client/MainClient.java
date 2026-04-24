package client;

import com.formdev.flatlaf.FlatDarkLaf;
import common.FeeService;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Client application entry point.
 * Sets up FlatLaf dark theme and connects to RMI server.
 */
public class MainClient {

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 1099;
    public static final String SERVICE_NAME = "FeeService";

    public static void main(String[] args) {
        // Setup FlatLaf Dark theme
        try {
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("FlatLaf init failed, using default L&F");
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Connect to RMI server
                Registry registry = LocateRegistry.getRegistry(SERVER_HOST, SERVER_PORT);
                FeeService service = (FeeService) registry.lookup(SERVICE_NAME);

                // Launch login form
                new LoginForm(service).setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Cannot connect to server at " + SERVER_HOST + ":" + SERVER_PORT +
                    "\nPlease ensure the server is running.\n\nError: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
