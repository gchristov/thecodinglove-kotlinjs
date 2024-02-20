# Exports required CI environment secrets to local environment files so that the project can use them
set -e
# Credentials
echo "$GCP_SA_KEY_APP" >> ./credentials-gcp-app.json
echo "$GCP_SA_KEY_INFRA" >> ./pulumi/credentials-gcp-infra.json
# Slack
echo SLACK_SIGNING_SECRET="$SLACK_SIGNING_SECRET" >> ./slack/adapter/secrets.properties
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> ./slack/adapter/secrets.properties
echo SLACK_CLIENT_SECRET="$SLACK_CLIENT_SECRET" >> ./slack/adapter/secrets.properties
# Monitoring
echo MONITORING_SLACK_URL="$MONITORING_SLACK_URL" >> ./common/monitoring/secrets.properties