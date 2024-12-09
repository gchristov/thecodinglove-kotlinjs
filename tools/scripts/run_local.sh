# Builds and runs the project locally with Docker
set -e
# This Gradle task runs across all composite build projects and because it's invoked from
# the root it'll try to use concurrent yarn instances that all try to write to the same cache
# folder. A single Gradle worker avoids this at the cost of a slower build but this is okay for now.
echo "🛠 Build project" && ./gradlew --max-workers=1 assemble
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