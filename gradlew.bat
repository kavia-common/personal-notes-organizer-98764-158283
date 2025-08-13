@echo off
REM Proxy script to delegate gradle wrapper commands to the notes_android_app module.

setlocal enabledelayedexpansion
set SCRIPT_DIR=%~dp0
set APP_DIR=%SCRIPT_DIR%notes_android_app

if not exist "%APP_DIR%\\gradlew.bat" (
  echo Error: Gradle wrapper not found at %APP_DIR%\gradlew.bat 1>&2
  exit /b 127
)

pushd "%APP_DIR%"
call gradlew.bat %*
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%
