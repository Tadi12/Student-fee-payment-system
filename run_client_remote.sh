#!/bin/bash

# Check if Java is installed
if ! command -v java &> /dev/null
then
    echo "Java (JDK) is not installed or not in PATH. Please install it to run this client."
    exit 1
fi

# Ask for Server IP
read -p "Enter Server IP Address: " server_ip

echo "Starting Fee Payment Client connecting to $server_ip..."

# Use : as separator for classpath on Linux/macOS
java -cp "bin:lib/*" client.MainClient "$server_ip"
