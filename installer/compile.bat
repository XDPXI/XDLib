@echo off
set JAVA_FILE=XDLibInstaller.java
set CLASS_FILES=XDLibInstaller*.class
set MANIFEST_FILE=manifest.txt
set JAR_FILE=XDLibInstaller.jar
set FLATLAF_JAR=flatlaf-3.5.2.jar

echo Compiling %JAVA_FILE%...
javac -cp %FLATLAF_JAR% -Xlint:deprecation %JAVA_FILE%
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Creating JAR file %JAR_FILE%...
echo Main-Class: XDLibInstaller > %MANIFEST_FILE%
jar cfm %JAR_FILE% %MANIFEST_FILE% %CLASS_FILES%
if %errorlevel% neq 0 (
    echo Failed to create JAR file!
    pause
    exit /b %errorlevel%
)

echo Running %JAR_FILE%...
java -cp "%JAR_FILE%;%FLATLAF_JAR%" XDLibInstaller
if %errorlevel% neq 0 (
    echo Failed to run JAR file!
    pause
    exit /b %errorlevel%
)

echo Cleaning up...
del %CLASS_FILES%
del %MANIFEST_FILE%

echo Done!
pause