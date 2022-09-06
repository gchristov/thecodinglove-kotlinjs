mkdir -p functions
./gradlew assemble
cp package.json functions/package.json
cp package-lock.json functions/package-lock.json
cd functions && npm install && cd ..
firebase serve