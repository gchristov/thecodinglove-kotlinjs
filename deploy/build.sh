# Builds the project and moves executable files to a target folder
set -e
./gradlew assemble
rm -rf "$1"
mkdir -p "$1"
cp -R build/js/. "$1"