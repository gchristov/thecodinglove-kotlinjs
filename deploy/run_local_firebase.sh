# Builds and runs the project locally with the Firebase Emulator
set -e
sh ./deploy/build.sh functions
firebase serve