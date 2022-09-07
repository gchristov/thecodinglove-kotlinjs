# Builds Javascript code to a specific folder and installs dependencies
mkdir -p "$1"
./gradlew assemble
cp package.json "$1/package.json"
cp package-lock.json "$1/package-lock.json"
cd "$1" && npm install && cd ..
