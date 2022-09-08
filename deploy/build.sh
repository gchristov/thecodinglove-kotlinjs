# Builds Javascript code to a specific folder and installs dependencies
set -e
./gradlew assemble
cd "$1" && npm install && cd ..
cp build/js/packages/thecodinglove-kmp-appJs/kotlin/thecodinglove-kmp-appJs.js "$1/index.js"
