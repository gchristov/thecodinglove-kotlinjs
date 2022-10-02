# Builds and runs the project locally with the Firebase Emulator
set -e
sh ./scripts/build.sh
cd build/production && npm install && cd ..
firebase serve