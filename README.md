# About

`thecodinglove-kotlinjs` is a cutting-edge Kotlin multiplatform project, powering [TheCodingLove GIFs](https://slack.com/apps/AFNEWBNFN) Slack app. Built with [KotlinJS](https://kotlinlang.org/docs/js-overview.html), it seamlessly bridges Kotlin and Javascript to bring a fully serverless platform, currently deployed as microservice [Docker](https://www.docker.com/) containers on [Google Cloud](https://cloud.google.com/run) using [Pulumi](https://www.pulumi.com/) infrastructure as code.

<details>
  <summary>🛠 Tech stack</summary>

- [Hexagon microservice architecture](https://en.m.wikipedia.org/wiki/Hexagonal_architecture_(software)) - implemented as `domain`, `adapter` and `service` sub-projects for each microservice
- [KotlinJS](https://kotlinlang.org/docs/js-overview.html) - NodeJS transpiling
- [PubSub](https://cloud.google.com/pubsub) - event-driven messaging
- [Firestore](https://firebase.google.com/docs/firestore) - NoSQL database
- [Docker](https://www.docker.com/) - containerised deployment
- [Cloud Run](https://cloud.google.com/run) - serverless deployment of microservices
- [Cloud Scheduler](https://cloud.google.com/scheduler) - cron jobs
- [GitHub Actions](https://github.com/features/actions) - CI automation
- [Pulumi](https://www.pulumi.com/) - infrastructure as code, using [micro-stacks](https://www.pulumi.com/docs/using-pulumi/organizing-projects-stacks/#micro-stacks)
- [nginx](https://nginx.org/) - web reverse proxy
</details>

🌍 [Live demo](https://thecodinglove.crowdstandout.com)

## Setup

The project can be run locally and deployed on the cloud - in our case to Google Cloud via Pulumi. The local build talks to a Firestore database and sends PubSub messages, so the cloud setup is a prerequisite.

The below setup assumes you've already cloned the project locally.

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
4. [Signup and Install Pulumi](https://www.pulumi.com/docs/clouds/gcp/get-started/begin/#install-pulumi) locally.
5. Create a Pulumi [access token](https://www.pulumi.com/docs/pulumi-cloud/access-management/access-tokens/) and login locally using `pulumi login`.
6. The project uses Pulumi micro-stacks to deploy the microservices individually. Each microservice has a corresponding `infra` folder containing its `Pulumi.yaml` infrastructure program, eg `search/infra`. To get the project going, you will need to manually initialise each microservice on GCP using the Pulumi scripts. 
```
The order to do this matters, so go with common/infra first, then all other microservices, then proxy-web/infra. The reason is that the resouces are created incrementally at each stage and we currently have no way synchronize them.
```
7. The steps to deploy a microservice's infrastructure is the same for all:
   1. Navigate to its `infra` folder.
   2. Paste the `credentials-gcp-infra.json` file.
   3. Create a new empty Pulumi project with no resources using the `pulumi new` command and follow the instructions:
      - you can use the prompt `Empty project with no resources` for Pulumi AI;
      - you can use `prod` as your stack name;
   4. Replace the `name` in the microservice `Pulumi.yaml` with the value you entered in the prompt.
   5. Open `Pulumi.prod.yml` and replace the `gcp:project` value with your project id.
   6. Run `pulumi up` to automatically create the required microservice infrastructure.
   7. Repeat for the remaining microservices.
8. Find your auto-generated `firebase-adminsdk` Service Account and give it the following additional roles:
   - `Pub/Sub Publisher`, for publishing messages to PubSub topics;
9. Export a JSON API key for your `firebase-adminsdk` Service Account and call it `credentials-gcp-app.json` - the app will need it later.
</details>

<details>
  <summary>2️⃣ Slack setup</summary>

The project powers an [existing Slack app](https://slack.com/apps/AFNEWBNFN), so you'll need one in order to run it. 

1. Create a new Slack app.
2. You will need an SSH tunnel to your localhost for Slack's APIs. You can use [serveo.net](http://serveo.net) for free and configure it with this command `ssh -R YOUR_DOMAIN.serveo.net:80:localhost:3000 serveo.net`.
3. Point the following Slack features to the relevant project API endpoints that know how to respond to them using the url you used for [serveo.net](http://serveo.net):
   - [Slash commands](https://api.slack.com/slash-commands) -> `YOUR_DOMAIN.serveo.net/api/slack/slash`
   - [OAuth](https://api.slack.com/authentication/oauth-v2) -> `YOUR_DOMAIN.serveo.net/api/slack/auth`
   - [Events](http://api.slack.com/events-api) -> `YOUR_DOMAIN.serveo.net/api/slack/event`
   - [Interactivity](https://api.slack.com/messaging/interactivity) -> `YOUR_DOMAIN.serveo.net/api/slack/interactivity`
4. Make a note of your `Slack Client ID`, `Secret` and `Signing Secret`.
</details>

<details>
  <summary>3️⃣ Local setup</summary>

1. [Install Docker Desktop](https://docs.docker.com/get-started/) and start it up. No additional configuration is required as the project uses Docker Compose to run locally. Checkout the `docker` folder for the setup.
2. [Install IntelliJ](https://www.jetbrains.com/help/idea/installation-guide.html). This project has been tested with `IntelliJ IDEA 2023.2.5`.
3. Open the root project with IntelliJ and wait for it to initialise.
4. Create a Slack channel to receive server error messages and monitoring updates. The project is configured to post all unhandled `Throwable`s to that channel. We use the [Incoming Webhooks](https://slack.com/apps/A0F7XDUAZ-incoming-webhooks) app to obtain a channel URL webhook.
5. Create the following `secrets.properties` files:
```
# Under /slack/domain/secrets.properties
SLACK_SIGNING_SECRET=YOUR_SLACK_SIGNING_SECRET
SLACK_CLIENT_ID=YOUR_SLACK_CLIENT_ID
SLACK_CLIENT_SECRET=YOUR_SLACK_CLIENT_SECRET
```
```
# Under /slack-web/domain/secrets.properties
SLACK_CLIENT_ID=YOUR_SLACK_CLIENT_ID
```
```
# Under /common/monitoring/secrets.properties
MONITORING_SLACK_URL=YOUR_MONITORING_SLACK_URL
```
6. Copy the `credentials-gcp-app.json` Service Account JSON API key to the root project folder.
</details>

## Run locally

After completing the setup, you should be able to run the project locally using the `TheCodingLove-Docker` IntelliJ IDE configuration. There is a landing page that should be available when you navigate to your [serveo.net](http://serveo.net) url.

## CI and cloud deployment

This is really up to you! However, we've provided our setup below.

<details>
  <summary>GitHub Actions</summary>

The project is configured to build with [GitHub Actions](https://github.com/features/actions) and have a separate workflow for each microservice. Checkout the `.github` folder for details. Follow these steps to configure the CI environment:

1. Add your Pulumi access token as a [GitHub encrypted secret](https://docs.github.com/actions/automating-your-workflow-with-github-actions/creating-and-using-encrypted-secrets) with the name `PULUMI_ACCESS_TOKEN`.
2. Each of the variables defined in the `secrets.properties` files you created above should be exposed as GitHub encrypted secrets, using the same names as keys.
3. Add an additional `GCP_SA_KEY_INFRA` GitHub encrypted secret, containing the raw JSON API key for the above infrastructure as code Service Account.
4. Add an additional `GCP_SA_KEY_APP` GitHub encrypted secret, containing the raw JSON API key for the `firebase-adminsdk` Service Account.
5. (Optional) Install the [Pulumi GitHub app](https://www.pulumi.com/docs/using-pulumi/continuous-delivery/github-app/) to get automated summaries of your infrastructure as code changes directly on your PR.

Once this is done:
   - opening pull requests against the repo will trigger build/test checks as well as infrastructure changes preview for the microservice that has been changed;
   - merging pull requests to the main branch deploys the changes to the corresponding microservice to Google Cloud;
</details>
