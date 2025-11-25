@echo off
REM Batch script to update version across all project files

if "%~1"=="" (
    echo Usage: update-version.bat ^<new-version^>
    echo Example: update-version.bat 0.0.2-SNAPSHOT
    exit /b 1
)

set NEW_VERSION=%~1
set /p OLD_VERSION=<VERSION

echo Updating version from %OLD_VERSION% to %NEW_VERSION%...

REM Update VERSION file
echo %NEW_VERSION%> VERSION

REM Update .env files
if exist .env (
    powershell -Command "(Get-Content .env -Raw) -replace 'VERSION=.*', 'VERSION=%NEW_VERSION%' | Set-Content .env -NoNewline"
)
powershell -Command "(Get-Content .env.example -Raw) -replace 'VERSION=.*', 'VERSION=%NEW_VERSION%' | Set-Content .env.example -NoNewline"

REM Update root pom.xml
powershell -Command "(Get-Content pom.xml -Raw) -replace '<revision>.*</revision>', '<revision>%NEW_VERSION%</revision>' | Set-Content pom.xml -NoNewline"

REM Update service-discovery pom.xml if it exists
if exist service-discovery\pom.xml (
    powershell -Command "(Get-Content service-discovery/pom.xml -Raw) -replace '<revision>.*</revision>', '<revision>%NEW_VERSION%</revision>' | Set-Content service-discovery/pom.xml -NoNewline"
)

REM Update frontend package.json
powershell -Command "(Get-Content frontend/package.json -Raw) -replace '\"version\": \".*\"', '\"version\": \"%NEW_VERSION%\"' | Set-Content frontend/package.json -NoNewline"

echo.
echo Version updated successfully to %NEW_VERSION%
echo.
echo Next steps:
echo 1. Review changes: git diff
echo 2. Build: mvn clean install
echo 3. Commit: git add . ^&^& git commit -m "Bump version to %NEW_VERSION%"
