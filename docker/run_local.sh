# Builds and runs the project locally with Docker
set -e
echo "🛠 Build project" && ./gradlew assemble
echo "🧹 Clean up old Docker resources" && (docker image prune -af)
echo "🏁 Start local tunnel" && (ssh -tt -R codinglove.serveo.net:80:localhost:8080 serveo.net &)
sleep 1
echo "🏁 Start app" && echo "" && docker compose \
-f docker/landing-page-web-compose.yaml \
-f docker/proxy-web-compose.yaml \
-f docker/search-compose.yaml \
-f docker/self-destruct-compose.yaml \
-f docker/slack-compose.yaml \
-f docker/slack-web-compose.yaml \
-f docker/statistics-compose.yaml \
up --build --remove-orphans