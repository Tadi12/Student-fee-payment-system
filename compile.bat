@echo off
echo ============================================
echo   Compiling Student Fee Payment System
echo ============================================
echo.

if not exist bin mkdir bin

echo Compiling common package...
javac -cp "lib/*" -d bin src/common/*.java
if %errorlevel% neq 0 (echo FAILED: common package & exit /b 1)

echo Compiling util package...
javac -cp "bin;lib/*" -d bin src/util/*.java
if %errorlevel% neq 0 (echo FAILED: util package & exit /b 1)

echo Compiling server package...
javac -cp "bin;lib/*" -d bin src/server/*.java
if %errorlevel% neq 0 (echo FAILED: server package & exit /b 1)

echo Compiling client package...
javac -cp "bin;lib/*" -d bin src/client/*.java
if %errorlevel% neq 0 (echo FAILED: client package & exit /b 1)

echo.
echo ============================================
echo   Compilation Successful!
echo ============================================
echo.
echo Run the server:  run_server.bat
echo Run the client:  run_client.bat
