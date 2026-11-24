@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac --release 17 -encoding UTF-8 -d out src/app/*.java src/controller/*.java src/exception/*.java src/interfaces/*.java src/model/*.java src/view/*.java
if errorlevel 1 exit /b 1
java -cp out app.Principal
