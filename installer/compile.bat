@echo off
setlocal EnableDelayedExpansion

:: Configuration
set "JAVA_FILE=XDLibInstaller.java"
set "CLASS_FILES=XDLibInstaller*.class"
set "MANIFEST_FILE=manifest.txt"
set "JAR_FILE=XDLibInstaller.jar"
set "FLATLAF_JAR=flatlaf-3.5.2.jar"

:: Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

:: Check if required files exist
if not exist "%JAVA_FILE%" (
    echo Error: %JAVA_FILE% not found
    pause
    exit /b 1
)

if not exist "%FLATLAF_JAR%" (
    echo Error: %FLATLAF_JAR% not found
    pause
    exit /b 1
)

:: Clean any existing temp directory and create new one
if exist "temp" rmdir /s /q "temp"
mkdir temp

:: Extract FlatLaf JAR contents
echo [*] Extracting FlatLaf...
cd temp
jar xf "../%FLATLAF_JAR%"
cd ..

:: Compile with FlatLaf in classpath
echo [*] Compiling %JAVA_FILE%...
javac -cp "temp" "%JAVA_FILE%"
if %ERRORLEVEL% neq 0 (
    echo Error: Compilation failed
    rmdir /s /q temp
    pause
    exit /b %ERRORLEVEL%
)

:: Move class files to temp
move %CLASS_FILES% temp\ >nul

:: Create manifest
echo [*] Creating manifest...
(
    echo Manifest-Version: 1.0
    echo Main-Class: XDLibInstaller
    echo Class-Path: %FLATLAF_JAR%
) > "%MANIFEST_FILE%"

:: Create JAR with FlatLaf included
echo [*] Creating JAR file %JAR_FILE%...
cd temp
jar cfm "../%JAR_FILE%" "../%MANIFEST_FILE%" *
cd ..
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to create JAR file
    rmdir /s /q temp
    del /q "%MANIFEST_FILE%"
    pause
    exit /b %ERRORLEVEL%
)

:: Cleanup
echo [*] Cleaning up temporary files...
rmdir /s /q temp
del /q "%MANIFEST_FILE%"

echo [*] Done!
timeout /t 1 /nobreak >nul
echo [*] Closing in 3 seconds...
timeout /t 1 /nobreak >nul
echo [*] Closing in 2 seconds...
timeout /t 1 /nobreak >nul
echo [*] Closing in 1 second...
timeout /t 1 /nobreak >nul
echo [*] Closing...

endlocal
@echo on