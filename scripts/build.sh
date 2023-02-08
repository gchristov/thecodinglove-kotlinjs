# Builds the project and copies binaries to an output folder
set -e
./gradlew assemble
cp -R backend/appBackend/build/productionLibrary/. "build/productionBackend"
cp -R client/appWeb/appWeb/build/distributions/. "build/productionWeb"
cd build/productionBackend && npm install && cd ..