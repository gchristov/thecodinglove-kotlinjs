# Exports required CI environment secrets to local environment files so that the project can use them
set -e
echo "$GCP_SA_KEY_PUBSUB" >> ./local-credentials-pubsub.json
echo GCP_PROJECT_ID="$GCP_PROJECT_ID" >> ./local.properties
echo SLACK_SIGNING_SECRET="$SLACK_SIGNING_SECRET" >> ./local.properties
echo SLACK_REQUEST_VERIFICATION_ENABLED="$SLACK_REQUEST_VERIFICATION_ENABLED" >> ./local.properties
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> ./local.properties
echo SLACK_CLIENT_SECRET="$SLACK_CLIENT_SECRET" >> ./local.properties
echo SLACK_INTERACTIVITY_PUBSUB_TOPIC="$SLACK_INTERACTIVITY_PUBSUB_TOPIC" >> ./local.properties
echo SLACK_SLASH_COMMAND_PUBSUB_TOPIC="$SLACK_SLASH_COMMAND_PUBSUB_TOPIC" >> ./local.properties
echo APP_LOG_LEVEL="$APP_LOG_LEVEL" >> ./local.properties
echo APP_NETWORK_HTML_LOG_LEVEL="$APP_NETWORK_HTML_LOG_LEVEL" >> ./local.properties
echo APP_NETWORK_JSON_LOG_LEVEL="$APP_NETWORK_JSON_LOG_LEVEL" >> ./local.properties
echo APP_PUBLIC_URL="$APP_PUBLIC_URL" >> ./local.properties
echo SEARCH_PRELOAD_PUBSUB_TOPIC="$SEARCH_PRELOAD_PUBSUB_TOPIC" >> ./local.properties