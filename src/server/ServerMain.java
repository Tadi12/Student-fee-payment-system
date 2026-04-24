package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * RMI Server entry point.
 * Initializes DB, creates RMI registry, and binds FeeService.
 */
public class ServerMain {

    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "FeeService";

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   Student Fee Payment System - RMI Server   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Set RMI hostname if not already set via -Djava.rmi.server.hostname
            if (System.getProperty("java.rmi.server.hostname") == null) {
                try {
                    String localIp = java.net.InetAddress.getLocalHost().getHostAddress();
                    System.setProperty("java.rmi.server.hostname", localIp);
                    System.out.println("[Server] Auto-detected RMI hostname: " + localIp);
                } catch (Exception e) {
                    System.err.println("[Server] Warning: Could not auto-detect local IP. Client connections might fail.");
                }
            } else {
                System.out.println("[Server] Using RMI hostname: " + System.getProperty("java.rmi.server.hostname"));
            }

            // Initialize database
            System.out.println("[Server] Initializing database...");
            DBConnection.initializeDatabase();
            System.out.println();

            // Create or get RMI registry
            Registry registry;
            try {
                System.out.println("[Server] Creating RMI registry on port " + PORT + "...");
                registry = LocateRegistry.createRegistry(PORT);
            } catch (java.rmi.server.ExportException e) {
                System.out.println("[Server] Registry already exists, using existing one...");
                registry = LocateRegistry.getRegistry(PORT);
            }

            // Create and bind service
            FeeServerImpl service = new FeeServerImpl();
            registry.rebind(SERVICE_NAME, service);

            System.out.println("[Server] ✓ FeeService bound to RMI registry");
            System.out.println("[Server] ✓ Server is running on port " + PORT);
            System.out.println("[Server] Waiting for client connections...");
            System.out.println();

        } catch (Exception e) {
            System.err.println("[Server] FATAL: Failed to start server");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
