#!/usr/bin/env bash
# Proxy script to delegate gradle wrapper commands to the notes_android_app module.
# This fixes CI environments that invoke ./gradlew from the workspace root.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${SCRIPT_DIR}/notes_android_app"

if [[ ! -x "${APP_DIR}/gradlew" ]]; then
  echo "Error: Gradle wrapper not found at ${APP_DIR}/gradlew" >&2
  exit 127
fi

cd "${APP_DIR}"
exec ./gradlew "$@"
