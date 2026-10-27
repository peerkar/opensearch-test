# opensearch-test
Java 25 / Maven project exercising the OpenSearch Java client 3.9.0 (version set in pom.xml).

## Goal
Add OpenSearch features one at a time. Each is a small increment: minimal, readable at a glance,
consistent with the existing code. Do not over-engineeer.

## Conventions
- One class per domain, in its own singular-named package (`com.example.opensearch.<domain>`, e.g. `index`, `document`, `bulk`; not `indices`), constructed with an `OpenSearchClient`. Add methods to an existing class when the domain fits.
- Choose a class name that clearly describes its responsibility and be ready to justify it; avoid vague suffixes (Helper, Util) unless nothing more precise fits.
- Add methods to an existing class when the domain fits.
- Match existing style: descriptive full names, fields/logger at the bottom of the class, concise
  Javadoc on public members (one-line summary; @param/@return/@throws only where they add information).
- Reuse what exists. Propose any new dependency, abstraction or exception type before adding it.

- Errors:
  - Handle an exception where it is relevant and something can be done about it. The feature's expected error (e.g. create index → `resource_already_exists_exception`) is handled in the domain method: log it and return a result the caller can act on (e.g. `false`). Re-throw  anything else unchanged.
  - Let unrecoverable errors (bugs, broken packaged resources, connection failures) propagate to
    main(). Don't catch them just to reword the message.
  - Add a custom exception type only when a caller can act on it differently from a standard one, e.g. a bad file the user supplied. Otherwise use a standard type (`IllegalArgumentException`, `IllegalStateException`).
  - When wrapping a low-level exception, add context (which file, which index) and keep the cause. Keep the try block narrow: libraries can report  bad input with assorted runtime exceptions, not only the documented ones.
- Acknowledgements: check the acknowledged flags of write responses (`acknowledged`, and `shardsAcknowledged` where present) and log a warning when one is false, so the caller can act on it if necessary.
- Input validation: validate arguments simply at the public method (e.g. null/blank → `IllegalArgumentException`). Don't repeat rules OpenSearch already enforces, such as index naming rules.
- Read files and build inputs before the request builder lambda, not inside it, so local errors are clearly separate from request errors.
- Use the client's own JSON support (`<Type>._DESERIALIZER` with the client's `JsonpMapper`) instead of adding a JSON library. `withJson` is not available on every builder in 3.9.0.
- When changing a method signature, don't reorder parameters of the same type: existing calls
  would still compile but silently pass the wrong arguments.
- Comments explain why, not what (e.g. why a broad catch is needed). Skip comments that just repeat the code.

## Tests
- Pick the cheapest test that proves the behaviour, usually one or two per feature: unit tests (Surefire) for pure logic, integration tests (`*IT`, Failsafe) for anything that depends on OpenSearch.
- For each integration test, ask whether it should run against Testcontainers or the live cluster.
- Prefix test method names with the name of the method under test where possible (e.g. `createIndexReturnsFalseWhenIndexAlreadyExists`).
- Live cluster: configured by OPENSEARCH_URL / OPENSEARCH_USERNAME / OPENSEARCH_PASSWORD;  the test is skipped without them.
- Testcontainers: not set up yet. The first such test needs the dependency, and OpenSearchConfiguration currently forces HTTPS with credentials;  call both out in the plan.
- Report skipped tests as skipped, not passed.
- Before relying on library behaviour (which exceptions are thrown, what is ignored), check it with  a quick probe or test rather than assuming.
- Keep parsing and validation testable without a cluster, e.g. a package-private static helper that  takes a standalone `JacksonJsonpMapper`.

## Workflow per feature
1. Ask whether to commit the previously finished feature.
2. Show a short plan (changes, error handled, tests added and where they run; flag any change to existing production code) and wait for OK. If the request doesn't say where the feature is called from or which error matters, propose a default in the plan (usually: called from main(), plus the obvious expected error) instead of asking separately.
3. Implement, then run `./mvnw verify` to catch regressions.
4. List deliberately omitted items as "Next steps".

## Commits
- Never commit without asking. 
- Use Conventional Commits style messages.
- Keep commit messages concise: a subject line, plus a short body only when the subject isn't enough.
- Never add Claude as co-author (no `Co-Authored-By` trailer).
