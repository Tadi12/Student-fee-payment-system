# Student Fee Payment System

A distributed Student Fee Payment System using Java RMI, Swing (FlatLaf), MySQL, and Apache PDFBox.

## Architecture

```
┌─────────────────────┐       RMI (TCP 1099)       ┌─────────────────────┐
│   CLIENT (Swing)    │ ◄─────────────────────────► │   SERVER (RMI)      │
│                     │                              │                     │
│  LoginForm          │                              │  FeeServerImpl      │
│  AdminDashboard     │                              │    ├─ authenticate  │
│  StudentDashboard   │                              │    ├─ register      │
│  PaymentForm        │                              │    ├─ makePayment   │
│                     │                              │    └─ genReceipt    │
│  SwingWorker ───►   │                              │                     │
│  (async RMI calls)  │                              │  DBConnection ──►   │
└─────────────────────┘                              │    MySQL (JDBC)     │
                                                     └─────────────────────┘
```

## Technologies

- **Java SE 11+** — Core language
- **Java RMI** — Client-server communication
- **Swing + FlatLaf** — Modern dark-themed GUI
- **MySQL** — Database storage
- **JDBC** — Database connectivity
- **Apache PDFBox** — PDF receipt generation
- **SwingWorker** — Background threading for UI responsiveness

## Prerequisites

1. **Java JDK 11+** installed (`java` and `javac` on PATH)
2. **MySQL Server** running on `localhost:3306`
3. MySQL user `root` with empty password (or edit `DBConnection.java`)

### 1. Compile
Run the compilation script for your OS:
- **Windows**: `compile.bat`
- **Linux/macOS**: `./compile.sh`

> [!IMPORTANT]
> The **`lib/`** folder must contain all required `.jar` files. If you are missing these, the compilation will fail with "package does not exist" errors.

### 2. Start Server
```batch
run_server.bat
```
The server will:
- Create the `student_fee_db` database automatically
- Create all required tables
- Seed a default admin account: `admin` / `admin123`

### 3. Start Client
```batch
run_client.bat
```

## Default Credentials

| Role    | Username | Password  |
|---------|----------|-----------|
| Admin   | admin    | admin123  |

## Features

### Admin
- Secure login
- Register new students (auto-generates credentials)
- View all students with fee status
- View all payment transactions
- Dashboard with statistics

### Student
- Secure login
- View profile and fee balance
- Make fee payments
- View payment history
- Download PDF receipts

## Project Structure

```
├── src/
│   ├── client/          # Swing GUI (LoginForm, Dashboards, PaymentForm)
│   ├── server/          # RMI Server (FeeServerImpl, DBConnection)
│   ├── common/          # Shared DTOs + RMI Interface
│   └── util/            # Utilities (PDF, Password, Validation)
├── lib/                 # JAR dependencies
├── bin/                 # Compiled classes
├── receipts/            # Generated PDF receipts
├── compile.bat          # Build script
├── run_server.bat       # Start RMI server
└── run_client.bat       # Start client app
```

## Security

- Passwords hashed with SHA-256
- Role-based access control (Admin/Student)
- Server-side authorization enforcement
- Students cannot access admin features

## Team Collaboration

To contribute to this project or set it up on your local machine:

### 1. Clone the Repository
```bash
git clone <repository-url>
cd advanced-java-project
```

### 2. Database Setup
Ensure MySQL is running. The server automatically creates the database and tables on the first run. 
If you need to change database credentials, edit:
`src/server/DBConnection.java`

### 3. Running the App
Use the provided batch files:
- `compile.bat`: Compiles the source code.
- `run_server.bat`: Starts the RMI server.
- `run_client.bat`: Starts the GUI client.

### 4. Contributing
1. Create a new branch: `git checkout -b feature/your-feature-name`
2. Commit your changes: `git commit -m "Add some feature"`
3. Push to the branch: `git push origin feature/your-feature-name`
4. Open a Pull Request.

## Two-Machine Deployment

To run the system on two different machines (e.g., Server on Machine A, Client on Machine B):

### 1. On the Server Machine (Machine A)
1. Run `run_server.bat`.
2. Look for the line: `[Server] Auto-detected RMI hostname: <IP_ADDRESS>`.
3. Note down this IP address.

### 2. On the Client Machine (Machine B - Kubuntu/Linux)
1. Copy the project folder to Machine B.
2. Open a terminal in the project folder.
3. Make the scripts executable: `chmod +x *.sh`
4. Run the remote client: `./run_client_remote.sh`
5. When prompted, enter the **IP address** of Machine A.

**Note:** Ensure both machines are on the same WiFi/Network and that port `1099` is not blocked by a firewall on the Server machine.
