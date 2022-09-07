# Builds Javascript code to a specific folder and installs dependencies
set -e
./gradlew assemble
cp package.json "$1/package.json"
cp package-lock.json "$1/package-lock.json"
cd "$1" && npm install && cd ..
