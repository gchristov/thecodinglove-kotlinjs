# Builds and runs the API project locally with the Firebase Emulator
set -e
echo "🛠 Building..." && sh ./scripts/build.sh
echo "🏁 Starting local emulators..." && ssh -R 80:localhost:5000 serveo.net & ssh -R 80:localhost:5001 serveo.net & firebase emulators:start