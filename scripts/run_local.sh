# Builds and runs the project locally
set -e
image_tag="local-image"
echo "🧹 Clean up old containers" && (docker ps -aq | xargs docker stop && docker ps -aq | xargs docker rm && docker image prune -af)
echo "🛠 Build project" && ./gradlew assemble
echo "🛠 Build container image" && docker build --tag=$image_tag .
echo "🏁 Start local tunnel" && (ssh -tt -R 80:localhost:8080 serveo.net &) && sleep 1
# --init allows stopping the container with ctrl+c
echo "🏁 Start container" && echo "" && docker run --init -p 8080:8080 $image_tag
#echo "🏁 Starting local Firebase emulators..." && firebase emulators:start