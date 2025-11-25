# PowerShell script to update version across all project files

param(
    [Parameter(Mandatory=$true)]
    [string]$NewVersion
)

$ErrorActionPreference = "Stop"

$OldVersion = Get-Content VERSION -Raw
$OldVersion = $OldVersion.Trim()

Write-Host "Updating version from $OldVersion to $NewVersion..." -ForegroundColor Green

# Update VERSION file
Set-Content -Path VERSION -Value $NewVersion -NoNewline

# Update .env files
if (Test-Path .env) {
    (Get-Content .env -Raw) -replace "VERSION=.*", "VERSION=$NewVersion" | Set-Content .env -NoNewline
}
(Get-Content .env.example -Raw) -replace "VERSION=.*", "VERSION=$NewVersion" | Set-Content .env.example -NoNewline

# Update root pom.xml
(Get-Content pom.xml -Raw) -replace "<revision>.*</revision>", "<revision>$NewVersion</revision>" | Set-Content pom.xml -NoNewline

# Update frontend package.json
(Get-Content frontend/package.json -Raw) -replace "`"version`": `".*`"", "`"version`": `"$NewVersion`"" | Set-Content frontend/package.json -NoNewline

# Update K8s deployment files
Write-Host "Updating Kubernetes deployment files..." -ForegroundColor Cyan
Get-ChildItem k8s-configs/*-deployment.yaml | ForEach-Object {
    (Get-Content $_.FullName -Raw) -replace '\$\{VERSION:-[^}]*\}', "`${VERSION:-$NewVersion}" | Set-Content $_.FullName -NoNewline
}

Write-Host "Version updated successfully to $NewVersion" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Review changes: git diff"
Write-Host "2. Build: mvn clean install"
Write-Host "3. Commit: git add . && git commit -m 'Bump version to $NewVersion'"
