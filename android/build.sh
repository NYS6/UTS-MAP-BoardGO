#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

if [ -d "/c/Program Files/Android/Android Studio/jbr" ]; then
  export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
fi
if [ -d "/c/Users/nicoy/AppData/Local/Android/Sdk" ]; then
  export ANDROID_HOME="/c/Users/nicoy/AppData/Local/Android/Sdk"
  export ANDROID_SDK_ROOT="$ANDROID_HOME"
fi
if [ -n "${JAVA_HOME:-}" ]; then
  export PATH="$JAVA_HOME/bin:$PATH"
fi

if [ -x "./gradlew" ]; then
  ./gradlew "$@"
else
  GRADLE_BIN="/c/Users/nicoy/.gradle/wrapper/dists/gradle-9.5.0-bin/bvnork1r7n8i6kp5cnkibsc9q/gradle-9.5.0/bin/gradle"
  "$GRADLE_BIN" "$@"
fi
