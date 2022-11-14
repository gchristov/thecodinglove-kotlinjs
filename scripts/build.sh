# Builds the project and copies binaries to an output folder
set -e
./gradlew assemble
cp -R appJs/appApiJs/build/productionLibrary/. "build/productionApi"
cp -R appJs/appHtmlJs/build/distributions/. "build/productionHtml"
cd build/productionApi && npm install && cd ..