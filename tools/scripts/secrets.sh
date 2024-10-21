#!/bin/bash
set -e
# Exports required CI environment secrets to local secrets so that the project can use them

# Shared service GCP credentials
echo "$GCP_SA_KEY_APP" >> ./credentials-gcp-app.json

# Per-service infra GCP credentials
services=$(bash ./tools/scripts/list_services.sh)
for service in "${services[@]}"; do
  echo "$GCP_SA_KEY_INFRA" >> "$service/infra/credentials-gcp-infra.json"
done

## common library credentials
echo MONITORING_SLACK_URL="$MONITORING_SLACK_URL" >> common/monitoring/secrets.properties
echo GOOGLE_ANALYTICS_MEASUREMENT_ID="$GOOGLE_ANALYTICS_MEASUREMENT_ID" >> common/analytics/secrets.properties
echo GOOGLE_ANALYTICS_API_SECRET="$GOOGLE_ANALYTICS_API_SECRET" >> common/analytics/secrets.properties

# slack service credentials
echo SLACK_SIGNING_SECRET="$SLACK_SIGNING_SECRET" >> slack/domain/secrets.properties
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> slack/domain/secrets.properties
echo SLACK_CLIENT_SECRET="$SLACK_CLIENT_SECRET" >> slack/domain/secrets.properties

# slack-web service credentials
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> slack-web/domain/secrets.properties
