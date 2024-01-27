[![Deployment (GCP)](https://github.com/gchristov/thecodinglove-kotlinjs/actions/workflows/deploy.yml/badge.svg)](https://github.com/gchristov/thecodinglove-kotlinjs/actions/workflows/deploy.yml)

# About

`thecodinglove-kotlinjs` is a cutting-edge Kotlin multiplatform project, powering [TheCodingLove GIFs](https://slack.com/apps/AFNEWBNFN) Slack app. Built with [KotlinJS](https://kotlinlang.org/docs/js-overview.html), it seamlessly bridges Kotlin and Javascript to bring a fully serverless platform, currently deployed as a [Docker](https://www.docker.com/) container on [Google Cloud](https://cloud.google.com/run) using [Pulumi](https://www.pulumi.com/) infrastructure as code.

<details>
  <summary>🛠 Tech stack</summary>

- [KotlinJS](https://kotlinlang.org/docs/js-overview.html) - NodeJS transpiling
- [PubSub](https://cloud.google.com/pubsub) - event-driven messaging
- [Firestore](https://firebase.google.com/docs/firestore) - NoSQL database
- [Docker](https://www.docker.com/) - containerised deployment
- [Cloud Run](https://cloud.google.com/run) - serverless deployment of containers
- [Cloud Scheduler](https://cloud.google.com/scheduler) - cron jobs
- [GitHub Actions](https://github.com/features/actions) - CI automation
- [Pulumi](https://www.pulumi.com/) - infrastructure as code
- [nginx](https://nginx.org/) - API reverse proxy
</details>

🌍 [Live demo](https://thecodinglove.crowdstandout.com)

## Setup

The project can be run locally and on the cloud - in this case Google Cloud via Pulumi. The local build is generally independent but it still talks to a Firestore database and sends PubSub messages, so the cloud setup is still required.

<details>
  <summary>1️⃣ Google Cloud setup</summary>

1. Create a new Google Cloud project.
2. Create a Service Account for the infrastructure as code setup with the following roles:
   - `Artifact Registry Administrator`
   - `Firebase Admin`
   - `Service Account User`
   - `Service Usage Admin`
   - `Pub/Sub Admin`
   - `Cloud Scheduler Admin`
   - (Optional) If you're specifying a custom domain mapping, as we are, [verify domain ownership and add your service account as owner](https://search.google.com/search-console).
3. Export a JSON API key for your Service Account and call it `credentials-gcp-infra.json`.
4. [Signup and Install Pulumi](https://www.pulumi.com/docs/clouds/gcp/get-started/begin/#install-pulumi).
5. Create a Pulumi access token and login locally using `pulumi login`.
6. Create a new empty folder under the root of the project, called `infra` and `cd` into it.
7. Create an empty Pulumi project with no resources using the `pulumi new` command and follow the instructions:
   - you can use the prompt `Empty project with no resources` for Pulumi AI
   - you can use `prod` as your stack name
8. Replace the created `Pulumi.yaml` file with the one from the existing `pulumi` folder, preserving the original `name` and paste the Service Account JSON API key file there too.
9. Setup Pulumi with your Google Cloud project ID and credentials:
   - `pulumi config set gcp:credentials credentials-gcp-infra.json`
   - `pulumi config set gcp:project GCP_PROJECT_ID`
10. Run `pulumi up` to automatically create the required project infrastructure.
11. Find your new `firebase-adminsdk` Service Account and give it the following additional roles:
   - `Pub/Sub Publisher`, for publishing messages to PubSub topics
12. Export a JSON API key for your `firebase-adminsdk` Service Account and call it `credentials-gcp-app.json` - the app will need it later.
</details>

<details>
  <summary>2️⃣ Slack setup</summary>

The project powers an [existing Slack app](https://slack.com/apps/AFNEWBNFN), so you'll need one in order to run it. 

1. Create a new Slack app.
2. You will need an SSH tunnel to your localhost for Slack's APIs. You can use [serveo.net](http://serveo.net) for free and configure it with this command `ssh -R YOUR_DOMAIN.serveo.net:80:localhost:3000 serveo.net`.
3. Point the following Slack features to the relevant project APIs that know how to respond to them using the url you used for [serveo.net](http://serveo.net):
   - [Slash commands](https://api.slack.com/slash-commands) -> `YOUR_DOMAIN.serveo.net/api/slack/slash`
   - [OAuth](https://api.slack.com/authentication/oauth-v2) -> `YOUR_DOMAIN.serveo.net/api/slack/auth`
   - [Events](http://api.slack.com/events-api) -> `YOUR_DOMAIN.serveo.net/api/slack/event`
   - [Interactivity](https://api.slack.com/messaging/interactivity) -> `YOUR_DOMAIN.serveo.net/api/slack/interactivity`
4. Note down your `Slack Client ID`, `Secret` and `Signing Secret`.
</details>

<details>
  <summary>3️⃣ Local setup</summary>

1. [Install Docker Desktop](https://docs.docker.com/get-started/) and start it up. No additional configuration is required as the project uses Docker Compose to run locally. Checkout the `docker` folder for the setup.
2. [Install IntelliJ](https://www.jetbrains.com/help/idea/installation-guide.html). This project has been tested with `IntelliJ IDEA 2023.2.5`.
3. Clone the repository and open the project with IntelliJ.
4. Create a Slack channel to receive server error messages and monitoring updates. The project is configured to post all unhandled `Throwable`s to that channel. We use the [Incoming Webhooks](https://slack.com/apps/A0F7XDUAZ-incoming-webhooks) app.
5. Create a `env.properties` file at the root of the project with the following contents:
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
SEARCH_PRELOAD_PUBSUB_TOPIC=TOPIC_NAME
```
6. Copy the `credentials-gcp-app.json` Service Account JSON API key to the `app-service` folder.
</details>

## Run locally

After completing the setup, you should be able to run the project locally using the `TheCodingLove-Docker` IntelliJ IDE configuration. There is a landing page that should be available when you navigate to your [serveo.net](http://serveo.net) url.

## CI and cloud deployment

This is really up to you! However, we've provided our setup below.

<details>
  <summary>GitHub Actions</summary>

The project is configured to build with [GitHub Actions](https://github.com/features/actions). Checkout the `.github` folder for the workflows. Follow these steps to configure the CI environment:

1. Add your Pulumi access token as a [GitHub encrypted secret](https://docs.github.com/actions/automating-your-workflow-with-github-actions/creating-and-using-encrypted-secrets) with the name `PULUMI_ACCESS_TOKEN`.
2. Each of the variables defined in `env.properties` above should be exposed as GitHub encrypted secrets under the same names.
3. Add an additional `GCP_SA_KEY_INFRA` GitHub encrypted secret, containing the raw JSON API key for the above infrastructure as code Service Account.
4. Add an additional `GCP_SA_KEY_APP` GitHub encrypted secret, containing the raw JSON API key for the `firebase-adminsdk` Service Account.
5. (Optional) Install the [Pulumi GitHub app](https://www.pulumi.com/docs/using-pulumi/continuous-delivery/github-app/) to get automated summaries of your infrastructure as code changes directly on your PR.

Once this is done:
   - opening pull requests against the repo will trigger build/test checks as well as infrastructure changes preview
   - merging pull requests to the main branch deploys the app and any infrastructure changes to Google Cloud
</details>