FROM node:16-slim AS npm

ARG PORT
ARG API_URL
ARG SLACK_REQUEST_VERIFICATION
ARG SLACK_SLASH_COMMAND_PUBSUB_TOPIC
ARG SLACK_INTERACTIVITY_PUBSUB_TOPIC
# Args get wiped on multistage builds so we persist them in the environment
ENV PORT=${PORT}
ENV API_URL=${API_URL}
ENV SLACK_REQUEST_VERIFICATION=${SLACK_REQUEST_VERIFICATION}
ENV SLACK_SLASH_COMMAND_PUBSUB_TOPIC=${SLACK_SLASH_COMMAND_PUBSUB_TOPIC}
ENV SLACK_INTERACTIVITY_PUBSUB_TOPIC=${SLACK_INTERACTIVITY_PUBSUB_TOPIC}

WORKDIR /app

# Step 1: Install Node.js dependencies
COPY /productionExecutable/package.json .
RUN npm install --only=production

# Step 2: Copy app files
COPY /productionExecutable .

# Step 3: Run binaries
FROM npm AS run
WORKDIR /app
CMD exec node --enable-source-maps slack-service.js -port ${PORT} -apiUrl ${API_URL} -slackRequestVerification ${SLACK_REQUEST_VERIFICATION} -slackSlashCommandPubSubTopic ${SLACK_SLASH_COMMAND_PUBSUB_TOPIC} -slackInteractivityPubSubTopic ${SLACK_INTERACTIVITY_PUBSUB_TOPIC}
