#!/bin/bash
mkdir -p lib
echo "Downloading project libraries..."

# MySQL Connector
echo "Downloading MySQL Connector..."
curl -L -o lib/mysql-connector-j-8.3.0.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar

# FlatLaf
echo "Downloading FlatLaf..."
curl -L -o lib/flatlaf-3.5.4.jar https://repo1.maven.org/maven2/com/formdev/flatlaf/3.5.4/flatlaf-3.5.4.jar

# PDFBox
echo "Downloading PDFBox..."
curl -L -o lib/pdfbox-2.0.30.jar https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/2.0.30/pdfbox-2.0.30.jar

# FontBox
echo "Downloading FontBox..."
curl -L -o lib/fontbox-2.0.30.jar https://repo1.maven.org/maven2/org/apache/pdfbox/fontbox/2.0.30/fontbox-2.0.30.jar

# Commons Logging (Needed by PDFBox)
echo "Downloading Commons Logging..."
curl -L -o lib/commons-logging-1.2.jar https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar

echo "All libraries downloaded successfully!"
