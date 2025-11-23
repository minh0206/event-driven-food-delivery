@echo off
REM Script to deploy to Kubernetes with version validation

if "%VERSION%"=="" (
    echo ERROR: VERSION environment variable is not set!
    echo.
    echo Please set VERSION before deploying:
    echo   set /p VERSION=^<VERSION
    echo   or
    echo   set VERSION=0.0.2-SNAPSHOT
    echo.
    exit /b 1
)

echo Deploying with VERSION=%VERSION%

REM Deploy all services
for %%f in (k8s-configs\*-deployment.yaml) do (
    echo Applying %%f...
    powershell -Command "(Get-Content '%%f' -Raw) -replace '\${VERSION}', '%VERSION%' | kubectl apply -f -"
)

echo.
echo Deployment complete!
echo Check status: kubectl get pods
