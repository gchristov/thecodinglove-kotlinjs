# Builds the project and copies binaries to an output folder
set -e
./gradlew assemble
cp -R appJs/appJs/build/productionLibrary/. "build/production"
cd build/production && npm install && cd ..