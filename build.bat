@echo off
setlocal enabledelayedexpansion

:: Configuration
set "gradleFile=gradle.properties"
set "version="

:: Create Build Folder if it doesn't exist
echo [*] Preparing Build Folder
if not exist "build" mkdir build

:: Clean Build Folder
echo [*] Cleaning Build Folder
if exist "build" (
    del /q /f /s "build\*" >nul 2>&1
    for /d %%x in ("build\*") do rmdir /s /q "%%x" >nul 2>&1
)

:: Retrieve version from gradle.properties
echo [%date% %time%] Retrieving version from %gradleFile%...
for /f "usebackq tokens=* skip=5" %%a in ("%gradleFile%") do (
    if not defined version (
        set "line=%%a"
        set "version=!line:~8!"
    )
)

if not defined version (
    echo [!] Version not found in %gradleFile%. Exiting...
    exit /b
)

:: Clean up the version string
echo [%date% %time%] Cleaning up version string...
for /f "tokens=* delims= " %%a in ("!version!") do set "version=%%a"
echo [%date% %time%] Version found: !version!

:: Start Gradle Build
echo [*] Starting Gradle build...
cmd /c gradlew build --warning-mode all
if %errorlevel% neq 0 (
    echo [!] Error: Gradle build failed. Exiting...
    exit /b
)

:: Prepare file paths
set "fabricJar=fabric\build\libs\xdlib-fabric-1.21-%version%.jar"
set "neoforgeJar=neoforge\build\libs\xdlib-neoforge-1.21-%version%.jar"
set "forgeJar=forge\build\libs\XD's Library-forge-1.21-%version%.jar"
set "fabricJar1=fabric\build\libs\xdlib-fabric-1.21-%version%-javadoc.jar"
set "neoforgeJar1=neoforge\build\libs\xdlib-neoforge-1.21-%version%-javadoc.jar"
set "forgeJar1=forge\build\libs\XD's Library-forge-1.21-%version%-javadoc.jar"
set "fabricJar2=fabric\build\libs\xdlib-fabric-1.21-%version%-sources.jar"
set "neoforgeJar2=neoforge\build\libs\xdlib-neoforge-1.21-%version%-sources.jar"
set "forgeJar2=forge\build\libs\XD's Library-forge-1.21-%version%-sources.jar"

:: Move files
echo [%date% %time%] Moving files...

call :moveJar "%fabricJar%" "%fabricJar1%" "%fabricJar2%" "Fabric"
call :moveJar "%forgeJar%" "%forgeJar1%" "%forgeJar2%" "Forge"
call :moveJar "%neoforgeJar%" "%neoforgeJar1%" "%neoforgeJar2%" "NeoForge"

:: Rename files
echo [%date% %time%] Renaming files...
call :renameJar "build\xdlib-fabric-1.21-%version%.jar" "xdlib-fabric-%version%.jar"
call :renameJar "build\xdlib-fabric-1.21-%version%-javadoc.jar" "xdlib-fabric-%version%-javadoc.jar"
call :renameJar "build\xdlib-fabric-1.21-%version%-sources.jar" "xdlib-fabric-%version%-sources.jar"
call :renameJar "build\xdlib-neoforge-1.21-%version%.jar" "xdlib-neoforge-%version%.jar"
call :renameJar "build\xdlib-neoforge-1.21-%version%-javadoc.jar" "xdlib-neoforge-%version%-javadoc.jar"
call :renameJar "build\xdlib-neoforge-1.21-%version%-sources.jar" "xdlib-neoforge-%version%-sources.jar"
call :renameJar "build\XD's Library-forge-1.21-%version%.jar" "xdlib-forge-%version%.jar"
call :renameJar "build\XD's Library-forge-1.21-%version%-javadoc.jar" "xdlib-forge-%version%-javadoc.jar"
call :renameJar "build\XD's Library-forge-1.21-%version%-sources.jar" "xdlib-forge-%version%-sources.jar"

echo [%date% %time%] Build, file movement, and renaming complete!
echo [*] Process complete!
endlocal
exit /b

:moveJar
:: Function to move JAR files
:: Arguments: %1 = Main JAR, %2 = Javadoc JAR, %3 = Sources JAR, %4 = Display Name
set "mainJar=%~1"
set "javadocJar=%~2"
set "sourcesJar=%~3"
set "displayName=%4"

echo [*] Moving %displayName% JARs...
if exist "%mainJar%" (
    move "%mainJar%" "build\" >nul 2>&1
    move "%javadocJar%" "build\" >nul 2>&1
    move "%sourcesJar%" "build\" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [!] Error: Failed to move %displayName% JARs. Exiting...
        exit /b
    )
) else (
    echo [!] %displayName% JAR not found: %mainJar%
)
exit /b

:renameJar
:: Function to rename JAR files
:: Arguments: %1 = Source File, %2 = Target File
if exist "%~1" (
    ren "%~1" "%~2"
    if %errorlevel% neq 0 (
        echo [!] Error: Failed to rename %~1 to %~2. Exiting...
        exit /b
    )
) else (
    echo [!] File not found for renaming: %~1
)
exit /b