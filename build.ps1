# Set the version variable
$version = "7.0.0-SNAPSHOT"

# List of mods
$mods = @("fabric-1.21.1", "fabric-1.21.4", "fabric-1.21.8", "fabric-1.21.10", "neoforge-1.21.1")

# Run gradle clean build
Write-Host "Running 'gradle clean build'..."
gradle clean build

# Root temporary build folder
$rootTmp = Join-Path -Path (Get-Location) -ChildPath "build/tmp"

# Ensure root temp directory exists
if (-Not (Test-Path $rootTmp))
{
    New-Item -ItemType Directory -Path $rootTmp | Out-Null
}

# Ensure root build/libs folder exists
$finalLibs = Join-Path -Path (Get-Location) -ChildPath "build/libs"
if (-Not (Test-Path $finalLibs))
{
    New-Item -ItemType Directory -Path $finalLibs | Out-Null
}
else
{
    # Delete everything inside build/libs
    Get-ChildItem -Path $finalLibs -Recurse | Remove-Item -Force -Recurse
    Write-Host "Cleared existing contents of build/libs"
}

# Path to common jar
$commonJar = "common/build/libs/common-$version.jar"

# Process each mod folder
foreach ($mod in $mods)
{
    $modLibPath = Join-Path -Path $mod -ChildPath "build/libs"
    $modTmpPath = Join-Path -Path $rootTmp -ChildPath $mod

    # Create temp folder for this mod
    if (-Not (Test-Path $modTmpPath))
    {
        New-Item -ItemType Directory -Path $modTmpPath | Out-Null
    }

    # Move and extract the mod jar
    $jarPattern = "$mod-$version.jar"
    $jarFile = Join-Path -Path $modLibPath -ChildPath $jarPattern
    $tmpFile = Join-Path -Path $modTmpPath -ChildPath $jarPattern

    if (Test-Path $jarFile)
    {
        Write-Host "Extracting $jarFile to $modTmpPath..."
        Copy-Item -Path $jarFile -Destination $modTmpPath -Force
        Add-Type -AssemblyName System.IO.Compression.FileSystem
        [System.IO.Compression.ZipFile]::ExtractToDirectory($jarFile, $modTmpPath)

        # Delete the original jar after extraction
        Remove-Item -Path $tmpFile -Force
        Write-Host "Deleted original jar $jarFile after extraction."
    }
    else
    {
        Write-Warning "Jar $jarFile not found."
    }

    # Extract common jar contents to this mod folder, skip duplicates
    if (Test-Path $commonJar)
    {
        Write-Host "Merging common jar into $modTmpPath..."
        $commonTemp = Join-Path -Path $env:TEMP -ChildPath "common_extraction"
        if (Test-Path $commonTemp)
        {
            Remove-Item $commonTemp -Recurse -Force
        }

        New-Item -ItemType Directory -Path $commonTemp | Out-Null
        [System.IO.Compression.ZipFile]::ExtractToDirectory($commonJar, $commonTemp)

        # Copy files from common temp to mod tmp, skip duplicates
        Get-ChildItem -Path $commonTemp -Recurse | ForEach-Object {
            $relativePath = $_.FullName.Substring($commonTemp.Length + 1)
            $destPath = Join-Path -Path $modTmpPath -ChildPath $relativePath
            if (-Not (Test-Path $destPath))
            {
                if ($_.PSIsContainer)
                {
                    New-Item -ItemType Directory -Path $destPath | Out-Null
                }
                else
                {
                    Copy-Item -Path $_.FullName -Destination $destPath
                }
            }
        }

        Remove-Item $commonTemp -Recurse -Force
    }
    else
    {
        Write-Warning "Common jar $commonJar not found."
    }

    # Repack the folder into a jar in tmp
    $tempJar = Join-Path -Path $rootTmp -ChildPath "$mod-$version.jar"
    if (Test-Path $tempJar)
    {
        Remove-Item $tempJar -Force
    }

    Write-Host "Creating final jar $tempJar using Java jar command..."

    # Change to the mod temp directory
    Push-Location $modTmpPath

    # Ensure META-INF/MANIFEST.MF exists or create a minimal one
    $manifestPath = Join-Path -Path $modTmpPath -ChildPath "META-INF\MANIFEST.MF"
    if (-Not (Test-Path $manifestPath))
    {
        New-Item -ItemType Directory -Path (Split-Path $manifestPath) -Force | Out-Null
        Set-Content -Path $manifestPath -Value "Manifest-Version: 1.0`r`n"
    }

    # Run jar command to create the jar
    $jarCmd = "jar cf `"$tempJar`" -C `"$modTmpPath`" ."
    Write-Host "Running: $jarCmd"
    cmd /c $jarCmd

    # Return to original location
    Pop-Location

    # Move final jar to build/libs with prefix 'realevents-'
    $finalJar = Join-Path -Path $finalLibs -ChildPath "realevents-$mod-$version.jar"
    Move-Item -Path $tempJar -Destination $finalJar -Force
    Write-Host "Moved $mod-$version.jar as realevents-$mod-$version.jar to build/libs"
}

Write-Host "All mods processed. Final jars are in build/libs."
