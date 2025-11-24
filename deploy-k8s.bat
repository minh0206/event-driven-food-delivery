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

if "%NAMESPACE%"=="" (
    echo ERROR: NAMESPACE environment variable is not set!
    echo.
    echo Please set NAMESPACE before deploying:
    echo   set NAMESPACE=default
    echo   or
    echo   set NAMESPACE=production
    echo.
    exit /b 1
)

echo Deploying with VERSION=%VERSION%
echo Deploying to NAMESPACE=%NAMESPACE%
echo.

REM Create namespace if it doesn't exist
echo Checking namespace %NAMESPACE%...
kubectl get namespace %NAMESPACE% >nul 2>&1
if errorlevel 1 (
    echo Namespace %NAMESPACE% does not exist. Creating...
    kubectl create namespace %NAMESPACE%
    echo.
) else (
    echo Namespace %NAMESPACE% already exists.
    echo.
)

REM Delete existing deployments
echo Deleting existing deployments in namespace %NAMESPACE%...
for %%f in (k8s-configs\*-deployment.yaml) do (
    echo Deleting %%f...
    powershell -Command "(Get-Content '%%f' -Raw) -replace '\${VERSION}', '%VERSION%' -replace '\${NAMESPACE}', '%NAMESPACE%' | kubectl delete -f - --ignore-not-found=true"
    echo.
)
echo.
echo Waiting for resources to be deleted...
timeout /t 5 /nobreak >nul
echo.

REM Deploy all services
echo Deploying services...
for %%f in (k8s-configs\*-deployment.yaml) do (
    echo Applying %%f...
    powershell -Command "(Get-Content '%%f' -Raw) -replace '\${VERSION}', '%VERSION%' -replace '\${NAMESPACE}', '%NAMESPACE%' | kubectl apply -f -"
    echo.
)

echo Deployment complete!
echo Check status: kubectl get pods -n %NAMESPACE%
