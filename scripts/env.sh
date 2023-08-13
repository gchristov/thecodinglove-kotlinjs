# Exports required CI environment secrets to local environment files so that the project can use them
set -e
echo GCP_SA_KEY_PUBSUB="$GCP_SA_KEY_PUBSUB" >> ./local-credentials-pubsub.json
echo FIREBASE_API_KEY="$FIREBASE_API_KEY" >> ./local.properties
echo FIREBASE_AUTH_DOMAIN="$FIREBASE_AUTH_DOMAIN" >> ./local.properties
echo FIREBASE_PROJECT_ID="$FIREBASE_PROJECT_ID" >> ./local.properties
echo FIREBASE_STORAGE_BUCKET="$FIREBASE_STORAGE_BUCKET" >> ./local.properties
echo FIREBASE_GCM_SENDER_ID="$FIREBASE_GCM_SENDER_ID" >> ./local.properties
echo FIREBASE_APPLICATION_ID="$FIREBASE_APPLICATION_ID" >> ./local.properties
echo SLACK_SIGNING_SECRET="$SLACK_SIGNING_SECRET" >> ./local.properties
echo SLACK_REQUEST_VERIFICATION_ENABLED="$SLACK_REQUEST_VERIFICATION_ENABLED" >> ./local.properties
echo SLACK_CLIENT_ID="$SLACK_CLIENT_ID" >> ./local.properties
echo SLACK_CLIENT_SECRET="$SLACK_CLIENT_SECRET" >> ./local.properties
echo APP_LOG_LEVEL="$APP_LOG_LEVEL" >> ./local.properties
echo APP_NETWORK_HTML_LOG_LEVEL="$APP_NETWORK_HTML_LOG_LEVEL" >> ./local.properties
echo APP_NETWORK_JSON_LOG_LEVEL="$APP_NETWORK_JSON_LOG_LEVEL" >> ./local.properties