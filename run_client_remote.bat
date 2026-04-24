@echo off
set /p server_ip="Enter Server IP Address: "
echo Starting Fee Payment Client connecting to %server_ip%...
java -cp "bin;lib/*" client.MainClient %server_ip%
pause
