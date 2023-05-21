# Builds and runs the API project locally with some simulators and tunnels for external (eg Slack) access
set -e
echo "🛠 Building..." && sh ./scripts/build.sh
echo "🏁 Starting local website tunnel..." && (ssh -tt -R 80:localhost:5000 serveo.net &) && sleep 1
echo "🏁 Starting local API tunnel..." && (ssh -tt -R 80:localhost:5001 serveo.net &) && sleep 1
echo "🏁 Starting local Firebase emulators..." && firebase emulators:start