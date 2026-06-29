# Configuration
$version = "7.0.0-SNAPSHOT"
$group = "dev.xdpxi"
$mavenRepoUrl = "https://maven.xdpxi.net/releases"
$modrinthProjectId = "yDe2kPBC"
$modrinthApiUrl = "https://api.modrinth.com/v2"
$buildLibsDir = Join-Path (Get-Location) "build/libs"

# Credentials (use environment variables for security)
$mavenUsername = $env:MAVEN_USERNAME
$mavenPassword = $env:MAVEN_PASSWORD
$modrinthToken = $env:MODRINTH_TOKEN

if (-not $mavenUsername -or -not $mavenPassword)
{
    Write-Error "MAVEN_USERNAME and MAVEN_PASSWORD environment variables must be set"
    exit 1
}

# Maps loader type to its loaders and game versions
function Get-VersionMapping {
    param([System.IO.FileInfo[]]$jars)

    $mapping = @{}
    $order = @()

    foreach ($jar in $jars) {
        $name = $jar.Name

        # Skip common JAR
        if ($name -match "xdlib-common-") {
            continue
        }

        # Parse JAR name: xdlib-<loader>[-<mcversion>]-<version>.jar
        if ($name -match "xdlib-bukkit-$version\.jar") {
            $displayName = "$version-bukkit"
            $order += $displayName
            $mapping[$displayName] = @{
                jar = $jar
                loaders = @("paper", "folia", "spigot", "purpur")
                gameVersions = @("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11")
                dependencies = @()
            }
        }
        elseif ($name -match "xdlib-neoforge-(\d+\.\d+\.\d+)-$version\.jar") {
            $mcVersion = $matches[1]
            $displayName = "$version-neo-$mcVersion"
            $order += $displayName

            # Map neoforge versions to supported game versions
            $gameVersions = @()
            if ($mcVersion -eq "1.21.1") { $gameVersions = @("1.21", "1.21.1") }
            elseif ($mcVersion -eq "1.21.4") { $gameVersions = @("1.21.4") }
            elseif ($mcVersion -eq "1.21.8") { $gameVersions = @("1.21.6", "1.21.7", "1.21.8") }
            elseif ($mcVersion -eq "1.21.10") { $gameVersions = @("1.21.9", "1.21.10") }
            elseif ($mcVersion -eq "1.21.11") { $gameVersions = @("1.21.11") }

            $mapping[$displayName] = @{
                jar = $jar
                loaders = @("neoforge")
                gameVersions = $gameVersions
                dependencies = @()
            }
        }
        elseif ($name -match "xdlib-fabric-(\d+\.\d+\.\d+)-$version\.jar") {
            $mcVersion = $matches[1]
            $displayName = "$version-fabric-$mcVersion"
            $order += $displayName

            # Map fabric versions to supported game versions
            $gameVersions = @()
            if ($mcVersion -eq "1.21.1") { $gameVersions = @("1.21", "1.21.1") }
            elseif ($mcVersion -eq "1.21.4") { $gameVersions = @("1.21.4") }
            elseif ($mcVersion -eq "1.21.8") { $gameVersions = @("1.21.6", "1.21.7", "1.21.8") }
            elseif ($mcVersion -eq "1.21.10") { $gameVersions = @("1.21.9", "1.21.10") }
            elseif ($mcVersion -eq "1.21.11") { $gameVersions = @("1.21.11") }

            $fabricApiDep = @{
                version_id = $null
                project_id = "P7dR8mSH"
                file_name = $null
                dependency_type = "embedded"
            }

            $mapping[$displayName] = @{
                jar = $jar
                loaders = @("fabric", "quilt")
                gameVersions = $gameVersions
                dependencies = @($fabricApiDep)
            }
        }
    }

    # Sort by desired order: bukkit, neo versions, fabric versions
    $sortedMapping = [ordered]@{}
    $neoVersions = @("1.21.1", "1.21.4", "1.21.8", "1.21.10", "1.21.11")
    $fabricVersions = @("1.21.1", "1.21.4", "1.21.8", "1.21.10", "1.21.11")

    # Bukkit first
    if ($mapping.Contains("$version-bukkit")) {
        $sortedMapping["$version-bukkit"] = $mapping["$version-bukkit"]
    }

    # NeoForge versions in order
    foreach ($mcVer in $neoVersions) {
        $key = "$version-neo-$mcVer"
        if ($mapping.Contains($key)) {
            $sortedMapping[$key] = $mapping[$key]
        }
    }

    # Fabric versions in order
    foreach ($mcVer in $fabricVersions) {
        $key = "$version-fabric-$mcVer"
        if ($mapping.Contains($key)) {
            $sortedMapping[$key] = $mapping[$key]
        }
    }

    return $sortedMapping
}

# Run build
Write-Host "Running build..."
./build.ps1
if ($LASTEXITCODE -ne 0)
{
    Write-Error "Build failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}

Write-Host "Build completed successfully. Publishing JARs..."

# Verify build/libs exists
if (-not (Test-Path $buildLibsDir))
{
    Write-Error "Build libs directory not found: $buildLibsDir"
    exit 1
}

# Get all JARs from build/libs
$jars = Get-ChildItem $buildLibsDir -Filter "*.jar"
if ($jars.Count -eq 0)
{
    Write-Error "No JARs found in $buildLibsDir"
    exit 1
}

# Create Maven credentials
$mavenCredential = New-Object System.Management.Automation.PSCredential($mavenUsername, (ConvertTo-SecureString $mavenPassword -AsPlainText -Force))

# Publish to Maven
Write-Host "`nPublishing to Maven" -ForegroundColor Cyan
$mavenSuccessCount = 0
$mavenFailCount = 0

foreach ($jar in $jars)
{
    $jarName = $jar.Name
    $jarPath = $jar.FullName

    # Extract artifact name from jar name
    # xdlib-fabric-1.21.1-7.0.0-SNAPSHOT.jar -> fabric-1.21.1
    $artifact = $jarName -replace "xdlib-", "" -replace "-$version.jar", ""

    # Convert group to path (dev.xdpxi -> dev/xdpxi)
    $groupPath = $group -replace "\.", "/"

    # Build upload URL
    $uploadUrl = "$mavenRepoUrl/$groupPath/xdlib-$artifact/$version/$jarName"

    Write-Host "Publishing $jarName to Maven..."
    try
    {
        Invoke-WebRequest -Uri $uploadUrl -InFile $jarPath -Method Put -Credential $mavenCredential -ErrorAction Stop | Out-Null
        Write-Host "  Published successfully" -ForegroundColor Green
        $mavenSuccessCount++
    } catch
    {
        Write-Host "  Failed to publish: $($_.Exception.Message)" -ForegroundColor Red
        $mavenFailCount++
    }
}

# Publish to Modrinth
Write-Host "`nPublishing to Modrinth" -ForegroundColor Cyan

if (-not $modrinthToken)
{
    Write-Warning "MODRINTH_TOKEN environment variable not set. Skipping Modrinth publishing."
    $modrinthSuccessCount = 0
    $modrinthFailCount = 0
} else
{
    $modrinthSuccessCount = 0
    $modrinthFailCount = 0

    $versionMapping = Get-VersionMapping -jars $jars

    foreach ($displayName in $versionMapping.Keys)
    {
        $mapping = $versionMapping[$displayName]
        $jar = $mapping.jar
        $loaders = $mapping.loaders
        $gameVersions = $mapping.gameVersions
        $dependencies = $mapping.dependencies

        $jarPath = $jar.FullName
        $jarName = $jar.Name

        Write-Host "Publishing $jarName to Modrinth..."

        try
        {
            # Prepare multipart form data for Modrinth API
            $boundary = [guid]::NewGuid().ToString()
            $encoding = [System.Text.Encoding]::UTF8
            $memoryStream = [System.IO.MemoryStream]::new()

            # Build JSON data object
            $versionData = @{
                project_id = $modrinthProjectId
                name = $displayName
                version_number = $displayName
                changelog = "Automated release of $displayName"
                loaders = $loaders
                game_versions = $gameVersions
                dependencies = $dependencies
                version_type = "release"
                featured = $false
                file_parts = @("file")
            } | ConvertTo-Json -Compress

            # Helper function to write string to stream
            function Write-ToStream {
                param([System.IO.MemoryStream]$stream, [string]$text)
                $bytes = $encoding.GetBytes($text)
                $stream.Write($bytes, 0, $bytes.Length)
            }

            # Add data field
            Write-ToStream $memoryStream "--$boundary`r`n"
            Write-ToStream $memoryStream "Content-Disposition: form-data; name=`"data`"`r`n`r`n"
            Write-ToStream $memoryStream $versionData
            Write-ToStream $memoryStream "`r`n"

            # Add file field
            Write-ToStream $memoryStream "--$boundary`r`n"
            Write-ToStream $memoryStream "Content-Disposition: form-data; name=`"file`"; filename=`"$jarName`"`r`n"
            Write-ToStream $memoryStream "Content-Type: application/java-archive`r`n`r`n"

            # Append binary file content
            $fileBytes = [System.IO.File]::ReadAllBytes($jarPath)
            $memoryStream.Write($fileBytes, 0, $fileBytes.Length)

            # Add closing boundary
            Write-ToStream $memoryStream "`r`n--$boundary--`r`n"

            $bodyBytes = $memoryStream.ToArray()
            $memoryStream.Close()

            # Upload to Modrinth
            $uploadUrl = "$modrinthApiUrl/version"

            $headers = @{
                "Authorization" = $modrinthToken
            }

            $response = Invoke-WebRequest -Uri $uploadUrl `
                -Method Post `
                -Headers $headers `
                -ContentType "multipart/form-data; boundary=$boundary" `
                -Body $bodyBytes `
                -ErrorAction Stop

            Write-Host "  Published successfully" -ForegroundColor Green
            $modrinthSuccessCount++
        } catch
        {
            Write-Host "  Failed to publish: $($_.Exception.Message)" -ForegroundColor Red
            try {
                $errorResponse = $_.ErrorDetails.Message
                if ($errorResponse) {
                    Write-Host "    Error: $errorResponse" -ForegroundColor Gray
                }
            } catch { }
            $modrinthFailCount++
        }
    }
}

Write-Host "`nPublish Summary" -ForegroundColor Cyan
Write-Host "Maven:"
Write-Host "  Successful: $mavenSuccessCount"
Write-Host "  Failed: $mavenFailCount"
Write-Host "Modrinth:"
Write-Host "  Successful: $modrinthSuccessCount"
Write-Host "  Failed: $modrinthFailCount"
