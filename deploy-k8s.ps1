# PowerShell script to deploy to Kubernetes with version validation

param(
    [string]$Version = $env:VERSION
)

if ([string]::IsNullOrEmpty($Version)) {
    Write-Host "ERROR: VERSION is not set!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please provide VERSION:" -ForegroundColor Yellow
    Write-Host "  .\deploy-k8s.ps1 -Version 0.0.2-SNAPSHOT"
    Write-Host "  or"
    Write-Host "  `$env:VERSION = Get-Content VERSION -Raw; `$env:VERSION = `$env:VERSION.Trim()"
    Write-Host "  .\deploy-k8s.ps1"
    Write-Host ""
    exit 1
}

Write-Host "Deploying with VERSION=$Version" -ForegroundColor Green

# Deploy all services
Get-ChildItem k8s-configs\*-deployment.yaml | ForEach-Object {
    Write-Host "Applying $($_.Name)..." -ForegroundColor Cyan
    $content = (Get-Content $_.FullName -Raw) -replace '\$\{VERSION\}', $Version
    $content | kubectl apply -f -
}

Write-Host ""
Write-Host "Deployment complete!" -ForegroundColor Green
Write-Host "Check status: kubectl get pods"
