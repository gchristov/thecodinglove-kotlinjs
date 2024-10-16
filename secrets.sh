#!/bin/bash
set -e
# Exports required CI environment secrets to local secrets so that the project can use them

# GCP credentials
echo "$GCP_SA_KEY_APP" >> ./credentials-gcp-app.json
directories=(
  "common"
  "landing-page-web"
  "proxy-web"
  "search"
  "self-destruct"
  "slack"
  "slack-web"
  "statistics"
)
for dir in "${directories[@]}"; do
  echo "$GCP_SA_KEY_INFRA" >> "$dir/infra/credentials-gcp-infra.json"
done
# common credentials
echo MONITORING_SLACK_URL="$MONITORING_SLACK_URL" >> ./common/monitoring/secrets.properties
echo GOOGLE_ANALYTICS_MEASUREMENT_ID="$GOOGLE_ANALYTICS_MEASUREMENT_ID" >> ./common/analytics/secrets.properties
echo GOOGLE_ANALYTICS_API_SECRET="$GOOGLE_ANALYTICS_API_SECRET" >> ./common/analytics/secrets.properties
# slack credentials
echo SLACK_SIGNING_SECRET="$SLACK_SIGNING_SECRET" >> ./slack/domain/secrets.properties
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> ./slack/domain/secrets.properties
echo SLACK_CLIENT_SECRET="$SLACK_CLIENT_SECRET" >> ./slack/domain/secrets.properties
# slack-web credentials
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> ./slack-web/domain/secrets.properties
