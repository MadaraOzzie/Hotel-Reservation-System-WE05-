#!/bin/sh
set -eu
cd "$(dirname "$0")"
if [ -n "${JAVA_HOME:-}" ]; then HOTEL_JAVA="$JAVA_HOME/bin/java"; else HOTEL_JAVA=java; fi
export PORT="${PORT:-8095}"
exec "$HOTEL_JAVA" -jar target/hotel-reservation-system-1.0.0.jar
