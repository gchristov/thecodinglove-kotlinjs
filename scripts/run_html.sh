# Builds and runs the API project locally with the Firebase Emulator
set -e
sh ./scripts/build.sh
./gradlew :appHtmlJs:jsBrowserProductionRun --continuous