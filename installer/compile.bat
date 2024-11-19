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

:: Compile
echo [*] Compiling %JAVA_FILE%...
javac -cp "%FLATLAF_JAR%" -Xlint:deprecation "%JAVA_FILE%"
if %ERRORLEVEL% neq 0 (
    echo Error: Compilation failed
    pause
    exit /b %ERRORLEVEL%
)

:: Create JAR
echo [*] Creating JAR file %JAR_FILE%...
(
    echo Manifest-Version: 1.0
    echo Main-Class: XDLibInstaller
    echo Class-Path: %FLATLAF_JAR%
) > "%MANIFEST_FILE%"

jar cfm "%JAR_FILE%" "%MANIFEST_FILE%" %CLASS_FILES%
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to create JAR file
    pause
    exit /b %ERRORLEVEL%
)

:: Run JAR
echo [*] Running %JAR_FILE%...
java -jar "%JAR_FILE%"
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to run JAR file
    pause
    exit /b %ERRORLEVEL%
)

:: Cleanup
echo [*] Cleaning up temporary files...
del /q %CLASS_FILES% 2>nul
del /q %MANIFEST_FILE% 2>nul

echo [*] Done!
pause

endlocal