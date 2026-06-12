# Changelog

All notable changes to this SDK are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/) and
this project follows [Semantic Versioning](https://semver.org/).

## [0.2.0] - 2026-06-12

### Features

- Per-application endpoint cap (`maxEndpoints`) and Developer Portal
  event-catalog toggle (`showEventTypes`) on the applications resource,
  with tri-state update semantics (`maxEndpoints(int)` to set,
  `clearMaxEndpoints()` to remove the cap)

### Changed

- `Application`'s public constructor gained `maxEndpoints` and
  `showEventTypes` parameters; code constructing `Application` directly
  (rather than via deserialization) must pass the new arguments

## [0.1.2] - 2026-06-01

### Features

- Add Deliveries resource to the management client

### Documentation

- README polish ahead of GA

## [0.1.1] - 2026-05-25

### Features

- Add environments resource to the management client
- Expose optional environmentId on endpoint creation
- Embed workspace region in API keys for SDK auto-routing

## [0.1.0] - 2026-04-10

### Features

- Initial release: ingestion client (send, trigger, batches) and management
  client (endpoints, event types, applications, subscriptions, portal
  sessions) with webhook signature verification
