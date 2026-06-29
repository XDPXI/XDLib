# Configuration
$version = "7.0.0-SNAPSHOT"
$group = "dev.xdpxi"
$mavenRepoUrl = "https://maven.xdpxi.net/releases"
$buildLibsDir = Join-Path (Get-Location) "build/libs"

# Credentials (use environment variables for security)
$username = $env:MAVEN_USERNAME
$password = $env:MAVEN_PASSWORD

if (-not $username -or -not $password)
{
    Write-Error "MAVEN_USERNAME and MAVEN_PASSWORD environment variables must be set"
    exit 1
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

# Create credentials
$credential = New-Object System.Management.Automation.PSCredential($username, (ConvertTo-SecureString $password -AsPlainText -Force))

# Publish each JAR
$successCount = 0
$failCount = 0

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

    Write-Host "Publishing $jarName..."
    try
    {
        Invoke-WebRequest -Uri $uploadUrl -InFile $jarPath -Method Put -Credential $credential -ErrorAction Stop
        Write-Host "Published successfully" -ForegroundColor Green
        $successCount++
    } catch
    {
        Write-Host "Failed to publish: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
}

Write-Host "`nPublish Summary:"
Write-Host "  Successful: $successCount"
Write-Host "  Failed: $failCount"

if ($failCount -gt 0)
{
    exit 1
}

Write-Host "`nAll JARs published successfully!" -ForegroundColor Green
