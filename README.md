[![Deployment (GCP)](https://github.com/gchristov/thecodinglove-kotlinjs/actions/workflows/deploy_gcp.yml/badge.svg)](https://github.com/gchristov/thecodinglove-kotlinjs/actions/workflows/deploy_gcp.yml)

# About

`thecodinglove-kotlinjs` is a cutting-edge Kotlin multiplatform project, powering [TheCodingLove GIFs](https://slack.com/apps/AFNEWBNFN) Slack app. Built with [KotlinJS](https://kotlinlang.org/docs/js-overview.html), it seamlessly bridges Kotlin and Javascript to bring a fully serverless platform, currently deployed as a [Docker](https://www.docker.com/) container on [Google Cloud](https://cloud.google.com/run).

🌍 [Live demo](https://thecodinglove.crowdstandout.com)

## Setup

Follow the instructions below to setup the project in your environment.

### Cloud setup

<details>
  <summary>Using Google Cloud</summary>

The project is currently configured to run on Google Cloud, using Firestore as a database. It also uses PubSub which is automatically setup when the server starts.

1. Create a new Google Cloud project and enable the following APIs
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
</details>

### Local setup

<details>
  <summary>Required tooling</summary>

1. [Install Docker Desktop](https://docs.docker.com/get-started/) and start it up. No additional configuration is required as the project sets up its own image and cleans up the containers after each run
2. [Install IntelliJ](https://www.jetbrains.com/help/idea/installation-guide.html). This project has been tested with `IntelliJ IDEA 2023.2.5`
3. _(Optional)_ Configure [serveo.net](http://serveo.net) for local development using `ssh -R YOUR_DOMAIN.serveo.net:80:localhost:3000 serveo.net`. This is useful when you want your app to be accessible globally via a tunnel, which is great for for developing Slack apps, as you need to point them to a valid HTTPS server.
</details>

<details>
  <summary>Project installation</summary>

Just clone the repository and open the project with IntelliJ. That's it!
</details>

<details>
  <summary>Local environment</summary>

1. Create a `local.properties` file at the root of the project with the following contents
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
2. Create a `local-credentials-gcp.json` file at the root of the project with the contents of a new JSON API key for the `firebase-adminsdk` service account you created previously.
</details>

<details>
  <summary>Dependencies</summary>

Dependencies are automatically pulled when the project is ran. Updating dependency versions requires `./gradlew kotlinUpgradeYarnLock` to be executed manually in order for the lock file to be updated.
</details>

<details>
  <summary>Monitoring</summary>

The project is configured to report all `Throwable`s to a Slack channel specified via the `SLACK_MONITORING_URL` environment variable. This is mainly used for stability monitoring.
</details>

### API setup

<details>
  <summary>For Slack</summary>

Create a new Slack app and point the following Slack tools to the relevant project APIs that can respond to them:
- [Slash commands](https://api.slack.com/slash-commands) -> `project-domain.com/api/slack/slash`
- [OAuth](https://api.slack.com/authentication/oauth-v2) -> `project-domain.com/api/slack/auth`
- [Events](http://api.slack.com/events-api) -> `project-domain.com/api/slack/event`
- [Interactivity](https://api.slack.com/messaging/interactivity) -> `project-domain.com/api/slack/interactivity`
</details>

## Run locally

There are two ways to run the app locally
- run the `TheCodingLove` IntelliJ IDE configuration
- run the `scripts/run_local.sh` script from a Terminal

## CI and cloud deployment

This is really up to you! However we've provided the existing setup for the project below.

<details>
  <summary>Using GitHub Actions</summary>

The project is configured to build with [GitHub Actions](https://github.com/features/actions). Checkout the `.github` folder for the workflows. Follow these steps to configure the CI environment.

1. Each of the variables defined in `local.properties` should be exposed as [GitHub encrypted secrets](https://docs.github.com/actions/automating-your-workflow-with-github-actions/creating-and-using-encrypted-secrets) using the same keys.
2. Add an additional `GCP_SA_KEY_DEPLOY` GitHub encrypted secret, containing the raw JSON API key for the CI deployment service account
3. Add an additional `GCP_SA_KEY_APP` GitHub encrypted secret, containing the raw JSON API key for the `firebase-adminsdk` service account

Once this is setup, for this project:
- opening pull requests against the repo triggers build and test checks
- merging pull requests to the main branch deploys the changes to Google Cloud;
</details>

## FAQ and known issues

<details>
  <summary>Can I use the project as a template?</summary>

Sure! Just don't forget to ⭐️ the repo!
</details>

<details>
  <summary>What's the full tech stack?</summary>

- [KotlinJS](https://kotlinlang.org/docs/js-overview.html) - NodeJS transpiling
- [PubSub](https://cloud.google.com/pubsub) - event-driven messaging
- [Firestore](https://firebase.google.com/docs/firestore) - NoSQL database
- [Docker](https://www.docker.com/) - containerised deployment
- [Cloud Run](https://cloud.google.com/run) - serverless deployment of containers
- [GitHub Actions](https://github.com/features/actions) - CI automation
</details>

<details>
  <summary>Is there a simpler cloud setup?</summary>

There will be soon as we're planning to use Terraform to automate the outlined steps below.
</details>