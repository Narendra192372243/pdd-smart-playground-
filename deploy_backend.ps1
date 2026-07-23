# Backend Deployment Script for Smart Playground App
$targetDir = "C:\xampp\htdocs\smart_playground"
$targetV2Dir = "$targetDir\v2"

Write-Host "Deploying backend to $targetDir..." -ForegroundColor Cyan

# Create target directories if they don't exist
if (!(Test-Path $targetDir)) {
    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
    Write-Host "Created target directory: $targetDir" -ForegroundColor Green
}

if (!(Test-Path $targetV2Dir)) {
    New-Item -ItemType Directory -Force -Path $targetV2Dir | Out-Null
    Write-Host "Created v2 target directory: $targetV2Dir" -ForegroundColor Green
}

# Copy root backend PHP and SQL files
$sourceBackend = "c:\Users\narendra\AndroidStudioProjects\SmartPlaygroundBookingEquipmentRentalApp\backend"
Get-ChildItem -Path $sourceBackend -File | ForEach-Object {
    Copy-Item -Path $_.FullName -Destination $targetDir -Force
    Write-Host "Copied $($_.Name) -> $targetDir" -ForegroundColor Yellow
}

# Copy v2 backend files
$sourceBackendV2 = "$sourceBackend\v2"
if (Test-Path $sourceBackendV2) {
    Get-ChildItem -Path $sourceBackendV2 -File | ForEach-Object {
        Copy-Item -Path $_.FullName -Destination $targetV2Dir -Force
        Write-Host "Copied $($_.Name) -> $targetV2Dir" -ForegroundColor Yellow
    }
}

Write-Host "`nBackend deployment complete! Files are staged at $targetDir" -ForegroundColor Green
