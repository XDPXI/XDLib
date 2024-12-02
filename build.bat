@echo off
setlocal enabledelayedexpansion

set "gradleFile=gradle.properties"
set "version="

echo [*] Cleaning Build Folder
if exist "build" (
    del /q /f /s "build\*"
    for /d %%x in ("build\*") do rmdir /s /q "%%x"
)

echo [*] Building...
echo ----------------------------------
echo [%date% %time%] Running Gradle build...
cmd /c gradlew build --warning-mode all
if %errorlevel% neq 0 (
    echo [!] Error: Gradle build failed. Exiting...
    exit /b
)
echo ----------------------------------

echo [%date% %time%] Getting version from gradle.properties...
echo [*] Getting version...
for /f "skip=5 tokens=*" %%a in (%gradleFile%) do (
    if not defined version (
        set "line=%%a"
        set "version=!line:~12!"
        echo [*] Found version: !version!
    )
)

if not defined version (
    echo [!] Version not found in gradle.properties. Exiting...
    exit /b
)

echo [%date% %time%] Version found: !version!
echo [*] Cleaning up...
for /f "tokens=* delims= " %%a in ("!version!") do set version=%%a

echo [%date% %time%] Checking for directories...
if not exist "build" (
    echo [*] Build directory not found. Creating directory...
    mkdir "build" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [!] Error: Failed to create build directory. Exiting...
        exit /b
    )
)

set "fabricJar=fabric\build\libs\xdlib-fabric-%version%.jar"
set "neoforgeJar=neoforge\build\libs\xdlib-neoforge-%version%.jar"

echo [%date% %time%] Moving files...
echo [*] Moving fabric JAR...
if exist "%fabricJar%" (
    move "%fabricJar%" "build\" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [!] Error: Failed to move fabric JAR. Exiting...
        exit /b
    )
) else (
    echo [!] Fabric JAR not found: %fabricJar%
)

echo [*] Moving neoforge JAR...
if exist "%neoforgeJar%" (
    move "%neoforgeJar%" "build\" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [!] Error: Failed to move neoforge JAR. Exiting...
        exit /b
    )
) else (
    echo [!] Neoforge JAR not found: %neoforgeJar%
)

echo [%date% %time%] Build and file movement complete!
echo [*] Complete!