# Exports required CI environment secrets to local environment files so that the project can use them
set -e
# Credentials
echo "$GCP_SA_KEY_APP" >> ./credentials-gcp-app.json
echo "$GCP_SA_KEY_INFRA" >> ./pulumi/credentials-gcp-infra.json
# Slack
echo SLACK_SIGNING_SECRET="$SLACK_SIGNING_SECRET" >> ./slack/adapter/env.properties
echo SLACK_REQUEST_VERIFICATION_ENABLED="$SLACK_REQUEST_VERIFICATION_ENABLED" >> ./slack/adapter/env.properties
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> ./slack/adapter/env.properties
echo SLACK_CLIENT_SECRET="$SLACK_CLIENT_SECRET" >> ./slack/adapter/env.properties
echo SLACK_INTERACTIVITY_PUBSUB_TOPIC="$SLACK_INTERACTIVITY_PUBSUB_TOPIC" >> ./slack/adapter/env.properties
echo SLACK_SLASH_COMMAND_PUBSUB_TOPIC="$SLACK_SLASH_COMMAND_PUBSUB_TOPIC" >> ./slack/adapter/env.properties
echo SLACK_MONITORING_URL="$SLACK_MONITORING_URL" >> ./slack/adapter/env.properties
# Search
echo SEARCH_PRELOAD_PUBSUB_TOPIC="$SEARCH_PRELOAD_PUBSUB_TOPIC" >> ./search/adapter/env.properties