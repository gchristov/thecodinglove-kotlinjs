[![Deploy Build](https://github.com/gchristov/thecodinglove-kmp/actions/workflows/deploy.yml/badge.svg)](https://github.com/gchristov/thecodinglove-kmp/actions/workflows/deploy.yml)

# Resources

  - [KotlinJS NodeJS example](https://github.com/wadejensen/kotlin-nodejs-example)
  - [KotlinJS ExpressJS example](https://github.com/chrisnkrueger/kotlin-express)
  - [KotlinJS external mappings example](https://dev.to/mpetuska/js-in-kotlinjs-c4g)

# Setup

## Project

1. [Install Docker](https://docs.docker.com/get-started/) and start it up. No additional configuration is required as the app sets up its own image and cleans up the containers after each run
2. [Install IntelliJ](https://www.jetbrains.com/help/idea/installation-guide.html)
3. Clone the repository and open the project with IntelliJ
4. Configure [serveo.net](http://serveo.net) for local development - `ssh -R codinglove.serveo.net:80:localhost:3000 serveo.net`. This is useful when you want your app to be accessible globally via a tunnel, eg for developing Slack apps in this case.

## Environment

The project is configured to be deployed and run on Google Cloud.
1. Create a Google Cloud project and enable the following APIs
    - `Cloud Run API`, used to run the final Docker container
    - `Artifact Registry API`, used to store the final Docker container
    - `Cloud Pub/Sub API`, used for communication between services
    - `Cloud Firestore API`, used for data storage
2. Create Service Account with these permissions for CI deployment
    - `Service Account User`
    - `Artifact Registry Administrator`
    - `Cloud Run Admin`
3. Create a second Service Account with these permissions for PubSub
    - `Pub/Sub Admin`
4. Setup Firestore
   - Create a Firestore database in Google Cloud
   - Navigate to your new Firestore database -> Security rules
   - Click `Enable Firebase` to allow editing of the rules. This will add two service accounts to your project:
     - `firebase-adminsdk-5-ramdom-chars@projectname.iam.gserviceaccount.com` - provides credentials for the Firebase Admin SDK
     - `firebase-service-account@firebase-sa-management.iam.gserviceaccount.com` - manages and links Firebase services to Google Cloud projects
   - Navigate to your project in Firebase -> Firebase Database -> Rules and allow reads and writes 
5. Create a `local.properties` file with the following contents
```
GCP_PROJECT_ID=
SLACK_SIGNING_SECRET=
SLACK_REQUEST_VERIFICATION_ENABLED=
SLACK_CLIENT_ID=
SLACK_CLIENT_SECRET=
SLACK_INTERACTIVITY_PUBSUB_TOPIC=
SLACK_SLASH_COMMAND_PUBSUB_TOPIC=
APP_LOG_LEVEL=
APP_NETWORK_HTML_LOG_LEVEL=
APP_NETWORK_JSON_LOG_LEVEL=
APP_PUBLIC_URL=
SEARCH_PRELOAD_PUBSUB_TOPIC=
```
6. Create a `local-credentials-pubsub.json` file with the contents of the JSON API key for the PubSub service account.

# Run

There are two ways to run the app locally
- run the `TheCodingLove` configuration, or
- run the `scripts/run_local.sh` script from a Terminal