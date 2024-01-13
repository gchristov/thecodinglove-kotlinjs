# Builds and runs the project locally with Docker
set -e
image_tag="local-image"
echo "🛠 Build project" && ./gradlew assemble
echo "🧹 Clean up old containers" && (docker ps -aq | xargs docker stop && docker ps -aq | xargs docker rm && docker image prune -af)
echo "🛠 Build container image" && docker build --tag=$image_tag ./docker
echo "🏁 Start local tunnel" && (ssh -tt -R codinglove.serveo.net:80:localhost:8080 serveo.net &) && sleep 1
# --init allows stopping the container with ctrl+c
echo "🏁 Start container" && echo "" && docker run --init -p 8080:8080 $image_tag