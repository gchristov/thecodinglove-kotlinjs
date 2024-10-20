# Builds and runs the project locally with Docker
set -e
echo "🛠 Build project" && ./gradlew assemble
echo "🧹 Clean up old Docker resources" && (docker image prune -af)
echo "🏁 Start local tunnel" && (ssh -tt -R codinglove.serveo.net:80:localhost:8080 serveo.net &)
sleep 1
echo "🏁 Start app" && echo "" && docker compose \
-f tools/docker/landing-page-web-compose.yaml \
-f tools/docker/proxy-web-compose.yaml \
-f tools/docker/search-compose.yaml \
-f tools/docker/self-destruct-compose.yaml \
-f tools/docker/slack-compose.yaml \
-f tools/docker/slack-web-compose.yaml \
-f tools/docker/statistics-compose.yaml \
up --build --remove-orphans