@echo off
setlocal enabledelayedexpansion

set "gradleFile=gradle.properties"
set "version="

echo [*] Building...
echo ----------------------------------
cmd /c gradlew build --warning-mode all
echo ----------------------------------

echo [*] Getting version...
for /f "skip=4 tokens=*" %%a in (%gradleFile%) do (
    if not defined version (
        set "line=%%a"
        set "version=!line:~8!"
    )
)

if not defined version (
    echo [!] Version not found in gradle.properties. Exiting...
    exit /b
)

echo [*] Cleaning up...
for /f "tokens=* delims= " %%a in ("!version!") do set version=%%a

echo [*] Checking for directories...
if not exist "build" (
    echo [*] Creating directory...
    mkdir "build" >nul 2>&1
)

set "fabricJar=fabric\build\libs\xdlib-fabric-1.21-%version%.jar"
set "forgeJar=forge\build\libs\XD's Library-forge-1.21-%version%.jar"
set "neoforgeJar=neoforge\build\libs\xdlib-neoforge-1.21-%version%.jar"

echo [*] Moving files...
if exist "%fabricJar%" move "%fabricJar%" "build\" >nul 2>&1
if exist "%forgeJar%" move "%forgeJar%" "build\" >nul 2>&1
if exist "%neoforgeJar%" move "%neoforgeJar%" "build\" >nul 2>&1

set "fabricJar=build\xdlib-fabric-1.21-%version%.jar"
set "forgeJar=build\XD's Library-forge-1.21-%version%.jar"
set "neoforgeJar=build\xdlib-neoforge-1.21-%version%.jar"

echo [*] Renaming files...
if exist "%fabricJar%" ren "%fabricJar%" "xdlib-fabric-%version%.jar"
if exist "%forgeJar%" ren "%forgeJar%" "xdlib-forge-%version%.jar"
if exist "%neoforgeJar%" ren "%neoforgeJar%" "xdlib-neoforge-%version%.jar"

echo [*] Complete!
pause