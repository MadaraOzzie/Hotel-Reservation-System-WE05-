#!/bin/sh
set -eu
cd "$(dirname "$0")"

APP_JAR="target/hotel-reservation-system-1.0.0.jar"
if [ ! -f "$APP_JAR" ]; then
  echo "Runnable JAR not found. Building the project from source..."
  if ! command -v mvn >/dev/null 2>&1; then
    echo "Maven 3.9+ is required to build. Install Maven and rerun this script." >&2
    exit 1
  fi
  sh build.sh
fi

if [ -n "${JAVA_HOME:-}" ]; then HOTEL_JAVA="$JAVA_HOME/bin/java"; else HOTEL_JAVA=java; fi
export PORT="${PORT:-8095}"
exec "$HOTEL_JAVA" -jar "$APP_JAR"
