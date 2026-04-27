# Contributing to nahook-java

Thanks for considering a contribution! A few important things to know first.

## Source of truth

This repository is a **subtree-split mirror** of the Java SDK from our private monorepo `getnahook/nahook`. PRs filed directly here **cannot be merged** — the next subtree-push from the monorepo will force-overwrite this branch.

## What we welcome

- **Bug reports** — open a GitHub issue with: reproduction steps, SDK version, Java version (`java -version`), OS.
- **Feature requests** — open an issue describing the use case and the API surface you'd want.
- **Small code suggestions** — paste a snippet in an issue and describe intent; we'll port it into the monorepo and credit you in the resulting commit.
- **Substantial patches** — email `support@nahook.com` first; we'll hand-port your change into the monorepo and credit you in the resulting commit.

## Local development

```bash
git clone https://github.com/getnahook/nahook-java
cd nahook-java
mvn compile
mvn test                              # full unit test suite
mvn package -DskipTests               # produces nahook-java-X.Y.Z.jar
```

`pom.xml` declares `<source>11</source>`. SDK supports Java 11+ and is published to Maven Central as `com.nahook:nahook-java`.

### Code style

- JUnit 5 for unit tests, jqwik for property-based tests
- Jackson for JSON serialization (don't introduce gson)
- No required formatter; match surrounding style

## License

By contributing, you agree your changes are released under the [MIT License](LICENSE).
