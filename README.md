[![Deployment (GCP)](https://github.com/gchristov/thecodinglove-kmp/actions/workflows/deploy_gcp.yml/badge.svg)](https://github.com/gchristov/thecodinglove-kmp/actions/workflows/deploy_gcp.yml)

# Resources

  - [KotlinJS NodeJS example](https://github.com/wadejensen/kotlin-nodejs-example)
  - [KotlinJS ExpressJS example](https://github.com/chrisnkrueger/kotlin-express)
  - [KotlinJS external mappings example](https://dev.to/mpetuska/js-in-kotlinjs-c4g)

# Setup

## Slack

This project powers an [existing Slack app](https://slack.com/apps/AFNEWBNFN). You can [follow steps here](https://api.slack.com/start/quickstart) to create one. Once you have the Slack app, you can use [Slack App Manifest](https://api.slack.com/reference/manifests) to setup the required bits:
  - [Slash commands](https://api.slack.com/slash-commands)
  - [OAuth](https://api.slack.com/authentication/oauth-v2)
  - [Events](http://api.slack.com/events-api)
  - [Interactivity](https://api.slack.com/messaging/interactivity)

## Project

1. [Install Docker](https://docs.docker.com/get-started/) and start it up. No additional configuration is required as the project sets up its own image and cleans up the containers after each run
2. [Install IntelliJ](https://www.jetbrains.com/help/idea/installation-guide.html). This project has been tested with `IntelliJ IDEA 2023.2.2`
3. Clone the repository and open the project with IntelliJ
4. Configure [serveo.net](http://serveo.net) for local development - `ssh -R YOUR_DOMAIN.serveo.net:80:localhost:3000 serveo.net`. This is useful when you want your app to be accessible globally via a tunnel, which is great for for developing Slack apps.

## Environment

The project is configured to be deployed and run on Google Cloud. It also uses Firestore as a database.

1. Create a Google Cloud project and enable the following APIs
    - `Cloud Run API`, used to run the final Docker container
    - `Artifact Registry API`, used to store the final Docker container
    - `Cloud Pub/Sub API`, used for communication between services
    - `Cloud Firestore API`, used for data storage
2. Create Service Account with these permissions for CI deployment
    - `Service Account User`
    - `Artifact Registry Administrator`
    - `Cloud Run Admin`
3. Setup Firestore
   - Create a Firestore database in Google Cloud
   - Navigate to your new Firestore database -> Security rules in Google Cloud
   - Click `Enable Firebase` to allow editing of the rules. This will add two service accounts to your project:
     - `firebase-adminsdk-5-random-chars@projectname.iam.gserviceaccount.com` - provides credentials for the Firebase Admin SDK. We will use this account's API key on the backend so you can rename it to something clearer, like `App backend`
     - `firebase-service-account@firebase-sa-management.iam.gserviceaccount.com` - manages and links Firebase services to Google Cloud projects 
   - Navigate to your project in Firebase -> Firebase Database -> Rules and copy the following setup
   ```
   rules_version = '2';
   service cloud.firestore {
      match /databases/{database}/documents {
         match /{document=**} {
            // The backend uses the Firebase Admin SDK, so external access is disabled  
            allow read, write: if false;
         }
      }
   }
   ```
4. Navigate to IAM and admin and give your `firebase-adminsdk` account the following additional roles
   - `Pub/Sub Admin`, for managing the PubSub subscriptions
5. Create a `local.properties` file at the root of the project with the following contents
```
SLACK_SIGNING_SECRET=YOUR_SLACK_SIGNING_SECRET
SLACK_REQUEST_VERIFICATION_ENABLED=true|false
SLACK_CLIENT_ID=YOUR_SLACK_CLIENT_ID
SLACK_CLIENT_SECRET=YOUR_SLACK_CLIENT_SECRET
SLACK_INTERACTIVITY_PUBSUB_TOPIC=TOPIC_NAME
SLACK_SLASH_COMMAND_PUBSUB_TOPIC=TOPIC_NAME
SLACK_MONITORING_URL=YOUR_SLACK_MONITORING_URL
APP_LOG_LEVEL=debug|verbose|error|info
APP_NETWORK_HTML_LOG_LEVEL=all|info|none
APP_NETWORK_JSON_LOG_LEVEL=all|info|none
APP_PUBLIC_URL=YOUR_PUBLIC_APP_URL
SEARCH_PRELOAD_PUBSUB_TOPIC=TOPIC_NAME
```
6. Create a `local-credentials-gcp.json` file at the root of the project with the contents of a new JSON API key for the `firebase-adminsdk` service account.

## CI

The project is configured to build with [GitHub Actions](https://github.com/features/actions). Checkout the `.github` folder for the workflows. Follow these steps to configure the CI environment.

1. Expose each of the variables defined in `local.properties` should be exposed as [GitHub encrypted secrets](https://docs.github.com/actions/automating-your-workflow-with-github-actions/creating-and-using-encrypted-secrets) using the same keys. 
2. Add an additional `GCP_SA_KEY_DEPLOY` GitHub encrypted secret, containing the raw JSON API key for the CI deployment service account
3. Add an additional `GCP_SA_KEY_APP` GitHub encrypted secret, containing the raw JSON API key for the `firebase-adminsdk` service account

## Dependencies

Dependencies are automatically pulled when the project is ran. Updating dependency versions requires `./gradlew kotlinUpgradeYarnLock` to be executed manually in order for the lock file to be updated.

# Run

There are two ways to run the app locally
- run the `TheCodingLove` IntelliJ IDE configuration
- run the `scripts/run_local.sh` script from a Terminal

# Deploy

- Opening pull requests against the repo triggers build and test checks.
- Merging pull requests to the main branch deploys the changes to Google Cloud.
