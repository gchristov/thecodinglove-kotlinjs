# thecodinglove-kotlinjs

Kotlin Multiplatform project powering the TheCodingLove Slack app. KotlinJS/IR transpiles to Node.js; microservices run as Docker containers on Google Cloud Run.

## Project layout

```
/                        ← root (composite build aggregator)
├── common/              ← shared library modules (composite build)
│   ├── kotlin/          ← Kotlin extensions
│   ├── network/         ← Ktor HTTP client
│   ├── firebase/        ← Firestore + Firebase Admin
│   ├── pubsub/          ← Google Cloud Pub/Sub
│   ├── analytics/       ← Google Analytics
│   ├── monitoring/      ← error reporting (posts to Slack webhook via common/slack)
│   ├── slack/           ← Slack HTTP API client (SlackSender, SlackMessage, SlackAuthToken)
│   └── test/            ← shared test utilities
├── slack/               ← Slack microservice (auth, message handling, GIF search results)
├── search/              ← Search microservice (scrapes thecodinglove.com)
├── self-destruct/       ← Self-destruct microservice (deletes messages after timeout)
├── statistics/          ← Statistics microservice (tracks usage)
├── slack-web/           ← Slack OAuth web flow microservice
├── landing-page-web/    ← Landing page
├── proxy-web/           ← nginx reverse proxy
└── tools/               ← developer tooling
    ├── docker/          ← Docker Compose files for local dev
    ├── scripts/         ← helper scripts (run_local.sh)
    └── slack/           ← Slack manifest + update_manifest.sh
```

Each microservice follows hexagonal architecture:
- `domain/` — interfaces (`Repository`), use cases, domain models, `BuildConfig` secrets
- `adapter/` — implementations (Firestore DB, HTTP handlers, PubSub handlers)
- `service/` — entry point, DI wiring, Docker image

## Composite builds

`common/` is a standalone Gradle composite build. Every microservice has `includeBuild("../common")` in its `settings.gradle.kts`.

**Critical distinction:**
- Inside `common/`, modules reference each other via `projects.*` (e.g. `projects.slack`)
- Microservice projects reference `common` modules via version catalog (e.g. `libs.common.slack`)
- New `common` modules must be added to `gradle/libs.versions.toml` under `[libraries]`

## DI framework

Kodein. Every module extends `DiModule()`:

```kotlin
object CommonSlackModule : DiModule() {
    override fun name() = "common-slack"
    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSlackSender(networkClient = instance()) }
        }
    }
}
```

Services wire modules in `*Service.kt`. Order matters — provide dependencies before consumers.

## Error handling

Arrow `Either<Throwable, T>` throughout. Use `either { }` DSL and `.bind()`.

## Visibility conventions

- `internal` for all implementation types (adapters, mappers, API models, DB models)
- Public only for domain interfaces, domain models, and DI modules

## Build commands

```bash
# From a microservice directory (e.g. cd slack):
./gradlew build          # compile + test
./gradlew jsTest         # run tests only (CI uses this)
./gradlew jsNodeTest     # run Node.js tests for a specific module

# From common/:
./gradlew build
./gradlew :slack:jsNodeTest   # test a specific common module
```

## Tests

Uses `kotlin.test` with mocha as the JS runner. JUnit XML reports are written to `build/test-results/jsNodeTest/TEST-*.xml`.

**Known quirk:** mocha prints `"0 passing (0ms)"` in the terminal — this is a dry-run pass by design (Kotlin/JS IR runs mocha twice; the first pass uses an empty adapter to verify the file loads, the second pass uses the TeamCity reporter which Gradle reads to generate the XML). Tests are passing if the XML shows `failures="0" errors="0"`.

Test files go in `src/commonTest/kotlin/`. Use `kotlin.test.assertEquals` etc. for pure functions; `runTest { }` for coroutines. No mocking — prefer real implementations or simple fakes.

## Secrets

Each microservice domain has a `secrets.properties` file (gitignored). Example:
```
# slack/domain/secrets.properties
SLACK_SIGNING_SECRET=...
SLACK_CLIENT_ID=...
SLACK_CLIENT_SECRET=...
```
These are read by the `BuildConfigPlugin` and exposed as `BuildConfig.*` constants.

## Common/slack module

`SlackSender` is the single public class for all Slack HTTP operations:
- `authUser(clientId, clientSecret, code)` — OAuth token exchange
- `postMessage(token, message)` — post to a channel
- `postMessageToUrl(url, message)` — post to a webhook URL
- `deleteMessage(token, channelId, timestamp)` — delete a message

`SlackMessage` has all fields defaulted — only specify what you need.

## Infrastructure

Each microservice has an `infra/` folder with a Pulumi YAML program (`Pulumi.yaml`) and a stack config (`Pulumi.prod.yaml`, stack name `prod`). Some services also have an `infra/dev/` folder with a separate program for dev-only resources (stack name `dev`, config in `Pulumi.dev.yaml`).

- `infra/` — prod resources (Cloud Run service, Pub/Sub topics/subscriptions, IAM). Deployed on merge to `master`.
- `infra/dev/` — dev-only resources (Pub/Sub topics/subscriptions with tunnel pushEndpoints for local development). Deployed on PRs.

The `infra/dev/` push endpoints point to a local tunnel URL. If that URL changes, it must be updated in all `infra/dev/` Pulumi files and in `tools/scripts/run_local.sh`.

GCP credentials (`credentials-gcp-infra.json`) sit alongside each `Pulumi.yaml`; `infra/dev/` programs reference the parent folder's credentials via `../credentials-gcp-infra.json`.

## CI

GitHub Actions per microservice. Tests run with:
```
./gradlew --no-daemon --continue --max-workers=1 jsTest
```
Results collected from `**/TEST-*.xml`.

On pull requests: build, test, preview `infra/` (stack `prod`), and deploy `infra/dev/` (stack `dev`) for any service that has that folder.
On merge to `master`: build, test, deploy `infra/` (stack `prod`).
