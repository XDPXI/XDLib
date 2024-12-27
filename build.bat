@echo off
setlocal enabledelayedexpansion

:: Configuration
set "gradleFile=gradle.properties"
set "version="
set "buildFolder=build"

:: Create Build Folder if it doesn't exist
echo [*] Preparing Build Folder
if not exist "%buildFolder%" mkdir "%buildFolder%"

:: Clean Build Folder
echo [*] Cleaning Build Folder
if exist "%buildFolder%" (
    del /q /f /s "%buildFolder%\*" >nul 2>&1
    for /d %%x in ("%buildFolder%\*") do rmdir /s /q "%%x" >nul 2>&1
)

:: Retrieve version from gradle.properties
echo [%date% %time%] Retrieving version from %gradleFile%...
for /f "usebackq tokens=* skip=5" %%a in ("%gradleFile%") do (
    if not defined version (
        set "line=%%a"
        set "version=!line:~8!"
        goto :versionFound
    )
)
:versionFound

if not defined version (
    echo [!] Version not found in %gradleFile%. Exiting...
    exit /b 1
)

:: Clean up the version string
echo [%date% %time%] Cleaning up version string...
set "version=%version: =%"
echo [%date% %time%] Version found: %version%

:: Start Gradle Build
echo [*] Starting Gradle build...
call gradlew build --warning-mode all
if %errorlevel% neq 0 (
    echo [!] Error: Gradle build failed. Exiting...
    exit /b 1
)

:: Define platforms and file types
set "platforms=fabric neoforge forge"
set "fileTypes=.jar -javadoc.jar -sources.jar"

:: Move and rename files
echo [%date% %time%] Moving and renaming files...
for %%p in (%platforms%) do (
    for %%t in (%fileTypes%) do (
        set "sourceFile=%%p\build\libs\xdlib-%%p-1.21-%version%%%t"
        if "%%p"=="forge" set "sourceFile=%%p\build\libs\XD's Library-%%p-1.21-%version%%%t"
        set "targetFile=%buildFolder%\xdlib-%%p-%version%%%t"
        call :moveAndRenameJar "!sourceFile!" "!targetFile!" "%%p"
    )
)

echo [%date% %time%] Build, file movement, and renaming complete!
echo [*] Process complete!
endlocal
exit /b 0

:moveAndRenameJar
:: Function to move and rename JAR files
:: Arguments: %1 = Source File, %2 = Target File, %3 = Display Name
if exist "%~1" (
    move "%~1" "%~2" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [!] Error: Failed to move and rename %~3 JAR. Exiting...
        exit /b 1
    )
) else (
    echo [!] %~3 JAR not found: %~1
)
exit /b 0