# Builds the project and copies binaries to an output folder
set -e
echo "🛠 Assembling..." && ./gradlew assemble
echo "📄 Copying backend files..." && cp -R backend/appBackend/build/productionLibrary/. "build/productionBackend"
echo "📄 Copying frontend files..." && cp -R client/appWeb/appWeb/build/distributions/. "build/productionWeb"
echo "🛠 Installing npm dependencies..." && cd build/productionBackend && npm install && cd ..