# Exports required CI environment secrets to local environment files so that the project can use them
set -e
echo "$GCP_SA_KEY_APP" >> ./app-service/credentials-gcp-app.json
echo "$GCP_SA_KEY_INFRA" >> ./pulumi/credentials-gcp-infra.json
echo SLACK_SIGNING_SECRET="$SLACK_SIGNING_SECRET" >> ./env.properties
echo SLACK_REQUEST_VERIFICATION_ENABLED="$SLACK_REQUEST_VERIFICATION_ENABLED" >> ./env.properties
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> ./env.properties
echo SLACK_CLIENT_SECRET="$SLACK_CLIENT_SECRET" >> ./env.properties
echo SLACK_INTERACTIVITY_PUBSUB_TOPIC="$SLACK_INTERACTIVITY_PUBSUB_TOPIC" >> ./env.properties
echo SLACK_SLASH_COMMAND_PUBSUB_TOPIC="$SLACK_SLASH_COMMAND_PUBSUB_TOPIC" >> ./env.properties
echo SLACK_MONITORING_URL="$SLACK_MONITORING_URL" >> ./env.properties
echo SEARCH_PRELOAD_PUBSUB_TOPIC="$SEARCH_PRELOAD_PUBSUB_TOPIC" >> ./env.properties