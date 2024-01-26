# Builds and runs the project locally with Docker
set -e
echo "🛠 Build project" && ./gradlew assemble
echo "🏁 Start local tunnel" && (ssh -tt -R codinglove.serveo.net:80:localhost:8080 serveo.net &)
sleep 1
echo "🏁 Start app" && echo "" && docker compose -f docker/compose.yaml up --build