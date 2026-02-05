# Configuration
$version = "7.0.0-SNAPSHOT" # MUST BE SAME VERSION FROM GRADLE PROPERTIES
$mods = @(
    "fabric-1.21.1",
    "fabric-1.21.4",
    "fabric-1.21.8",
    "fabric-1.21.10",
    "fabric-1.21.11",
    "neoforge-1.21.1",
    "neoforge-1.21.4",
    "neoforge-1.21.8",
    "neoforge-1.21.10",
    "neoforge-1.21.11"
)

$rootDir = Get-Location
$buildDir = Join-Path $rootDir "build"
$tmpRoot = Join-Path $buildDir "tmp"
$finalLibs = Join-Path $buildDir "libs"
$commonJar = Join-Path $rootDir "common/build/libs/common-$version.jar"

Add-Type -AssemblyName System.IO.Compression.FileSystem

# Gradle Build
./gradlew clean build

# Prepare directories
New-Item -ItemType Directory -Force -Path $tmpRoot, $finalLibs | Out-Null

Get-ChildItem $finalLibs -Recurse -Force -ErrorAction SilentlyContinue |
        Remove-Item -Recurse -Force

# Process mods
foreach ($mod in $mods)
{
    $modLibDir = Join-Path $rootDir "$mod/build/libs"
    $modTmpDir = Join-Path $tmpRoot $mod
    $jarName = "$mod-$version.jar"
    $modJar = Join-Path $modLibDir $jarName

    if (Test-Path $modTmpDir)
    {
        Remove-Item $modTmpDir -Recurse -Force
    }
    New-Item -ItemType Directory -Path $modTmpDir | Out-Null

    if (-not (Test-Path $modJar))
    {
        continue
    }

    [System.IO.Compression.ZipFile]::ExtractToDirectory($modJar, $modTmpDir)

    if (Test-Path $commonJar)
    {
        $commonTmp = Join-Path $tmpRoot "common_$mod"

        if (Test-Path $commonTmp)
        {
            Remove-Item $commonTmp -Recurse -Force
        }
        New-Item -ItemType Directory -Path $commonTmp | Out-Null

        [System.IO.Compression.ZipFile]::ExtractToDirectory($commonJar, $commonTmp)

        Get-ChildItem $commonTmp -Recurse | ForEach-Object {
            $relative = $_.FullName.Substring($commonTmp.Length + 1)
            $dest = Join-Path $modTmpDir $relative

            if (-not (Test-Path $dest))
            {
                if ($_.PSIsContainer)
                {
                    New-Item -ItemType Directory -Path $dest | Out-Null
                }
                else
                {
                    Copy-Item $_.FullName $dest
                }
            }
        }

        Remove-Item $commonTmp -Recurse -Force
    }
    else
    {
        Write-Warning "Common jar not found: $commonJar"
    }

    $manifest = Join-Path $modTmpDir "META-INF/MANIFEST.MF"
    if (-not (Test-Path $manifest))
    {
        New-Item -ItemType Directory -Force -Path (Split-Path $manifest) | Out-Null
        Set-Content $manifest "Manifest-Version: 1.0`r`n"
    }

    $tempJar = Join-Path $tmpRoot "$mod-$version.jar"
    $finalJar = Join-Path $finalLibs "xdlib-$mod-$version.jar"

    if (Test-Path $tempJar)
    {
        Remove-Item $tempJar -Force
    }

    Push-Location $modTmpDir
    & jar cf "$tempJar" -C "$modTmpDir" .
    Pop-Location

    Move-Item $tempJar $finalJar -Force
}
