#!/bin/bash
mkdir -p bin

echo "Compiling common package..."
javac -cp "lib/*" -d bin src/common/*.java

echo "Compiling util package..."
javac -cp "bin:lib/*" -d bin src/util/*.java

echo "Compiling server package..."
javac -cp "bin:lib/*" -d bin src/server/*.java

echo "Compiling client package..."
javac -cp "bin:lib/*" -d bin src/client/*.java

echo "Compilation Successful!"
