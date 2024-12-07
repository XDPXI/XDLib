@echo off
setlocal enabledelayedexpansion

set "gradleFile=gradle.properties"
set "version="
set "filePath=gradle.properties"
set "input_file=src\main\resources\plugin.yml"
set "output_file=src\main\resources\plugin_temp.yml"
set "line_number=0"

echo [*] Cleaning Build Folder
if exist "build" (
    del /q /f /s "build\*"
    for /d %%x in ("build\*") do rmdir /s /q "%%x"
)

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

echo [*] Building...
echo ----------------------------------
echo [%date% %time%] Running Gradle build...
cmd /c gradlew build --warning-mode all
if %errorlevel% neq 0 (
    echo [!] Error: Gradle build failed. Exiting...
    exit /b
)
cd spigot
echo mod_version=%version%>"%filePath%"
(for /f "delims=" %%a in (%input_file%) do (
    set /a line_number+=1
    set "line_content=%%a"

    rem Modify line 2 at column 11
    if !line_number! equ 2 (
        rem Extract part before column 11
        set "before=!line_content:~0,10!"
        rem Add %version% with a single quote at the end
        set "line_content=!before!%version%'"
    )

    echo !line_content!
)) > %output_file%

rem Overwrite the original file
move /y %output_file% %input_file% >nul 2>&1
cmd /c gradlew build --warning-mode all
if %errorlevel% neq 0 (
    echo [!] Error: Gradle build failed. Exiting...
    exit /b
)
cd ..
echo ----------------------------------

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
set "spigotJar=spigot\build\libs\XDLib-%version%.jar"

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

echo [*] Moving spigot JAR...
if exist "%spigotJar%" (
    move "%spigotJar%" "build\" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [!] Error: Failed to move spigot JAR. Exiting...
        exit /b
    )
    cd build
    ren "XDLib-%version%.jar" "xdlib-bukkit-%version%.jar" >nul 2>&1
    cd ..
) else (
    echo [!] Spigot JAR not found: %spigotJar%
)

echo [%date% %time%] Build and file movement complete!
echo [*] Complete!