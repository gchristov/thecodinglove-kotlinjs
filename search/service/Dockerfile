FROM node:16-slim AS npm

ARG PORT
ARG API_URL
ARG PRELOAD_SEARCH_PUBSUB_TOPIC
# Args get wiped on multistage builds so we persist them in the environment
ENV PORT=${PORT}
ENV API_URL=${API_URL}
ENV PRELOAD_SEARCH_PUBSUB_TOPIC=${PRELOAD_SEARCH_PUBSUB_TOPIC}

WORKDIR /app

# Step 1: Install Node.js dependencies
COPY /productionExecutable/package.json .
RUN npm install --only=production

# Step 2: Copy app files
COPY /productionExecutable .

# Step 3: Run binaries
FROM npm AS run
WORKDIR /app
CMD exec node --enable-source-maps search-service.js -port ${PORT} -apiUrl ${API_URL} -preloadSearchPubSubTopic ${PRELOAD_SEARCH_PUBSUB_TOPIC}
