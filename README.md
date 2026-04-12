# nahook-java

Official Java SDK for the [Nahook](https://nahook.com) webhook platform.

Two classes, one package:

| Class | Purpose | Auth |
|-------|---------|------|
| [`NahookClient`](#nahookclient) | Send and trigger webhook events | API key (`nhk_us_...`) |
| [`NahookManagement`](#nahookmanagement) | Manage endpoints, event types, apps | Management token (`nhm_...`) |

## Requirements

- Java 11+
- Jackson for JSON serialization

## Installation

### Maven

```xml
<dependency>
  <groupId>com.nahook</groupId>
  <artifactId>nahook-java</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.nahook:nahook-java:0.1.0'
```

---

## NahookClient

Send webhooks to specific endpoints or fan-out by event type.

### Setup

```java
import com.nahook.NahookClient;
import com.nahook.ClientOptions;
import java.time.Duration;

// Simple
NahookClient client = new NahookClient("nhk_us_...");

// With options
NahookClient client = new NahookClient("nhk_us_...", new ClientOptions()
    .timeout(Duration.ofSeconds(10))
    .retries(3));

// Builder pattern
NahookClient client = NahookClient.builder("nhk_us_...")
    .timeout(Duration.ofSeconds(10))
    .retries(3)
    .build();
```

### Configuration

The SDK automatically routes requests to the correct regional API based on your API key prefix (`nhk_us_...` -> US, `nhk_eu_...` -> EU, `nhk_ap_...` -> Asia Pacific). No configuration needed.

To override the base URL (for testing or local development):

```java
NahookClient client = NahookClient.builder("nhk_us_...")
    .baseUrl("http://localhost:3001")
    .build();
```

For unit tests, mock the SDK client at the dependency injection boundary. For integration tests, override the base URL to point at a local server.

### Send to a specific endpoint

```java
import com.nahook.SendOptions;

Map<String, Object> payload = Map.of("orderId", "123", "status", "paid");

SendResult result = client.send("ep_abc123", new SendOptions(payload)
    .idempotencyKey("order-123-paid")); // optional, auto-generated UUID if omitted
// result.getDeliveryId()    -> "del_..."
// result.getIdempotencyKey() -> "order-123-paid"
// result.getStatus()        -> "accepted"
```

### Fan-out by event type

```java
import com.nahook.TriggerOptions;

Map<String, Object> payload = Map.of("orderId", "123", "status", "paid");

TriggerResult result = client.trigger("order.paid", new TriggerOptions(payload)
    .metadata(Map.of("region", "us-east-1"))); // optional
// result.getEventTypeId()  -> "evt_..."
// result.getDeliveryIds()  -> ["del_..."]
// result.getStatus()       -> "accepted"
```

### Batch operations

```java
import com.nahook.SendBatchItem;
import com.nahook.TriggerBatchItem;

// Send to multiple endpoints (max 20 items)
BatchResult batch = client.sendBatch(List.of(
    new SendBatchItem("ep_abc", Map.of("orderId", "123")),
    new SendBatchItem("ep_def", Map.of("orderId", "456"))
));

// Fan-out multiple event types (max 20 items)
BatchResult fanOut = client.triggerBatch(List.of(
    new TriggerBatchItem("order.paid", Map.of("orderId", "123")),
    new TriggerBatchItem("order.shipped", Map.of("orderId", "456"))
));

// Results: 202 (all succeed) or 207 (mixed)
for (BatchResultItem item : batch.getItems()) {
    if (item.getError() != null) {
        System.out.println("Item " + item.getIndex() + " failed: " + item.getError().getCode());
    }
}
```

### Retry behavior

Retries are opt-in via the `retries` constructor option. When enabled:

- **Strategy:** Exponential backoff with full jitter
- **Delays:** 500ms base, 10s max
- **Retryable:** 5xx, 429 (respects `Retry-After`), network errors, timeouts
- **Non-retryable:** 400, 401, 403, 404, 409, 413
- **Safe by design:** Idempotency keys are always sent, making retries safe

---

## NahookManagement

Programmatically manage your Nahook workspace resources.

### Setup

```java
import com.nahook.NahookManagement;
import java.time.Duration;

// Simple
NahookManagement mgmt = new NahookManagement("nhm_...");

// Builder pattern
NahookManagement mgmt = NahookManagement.builder("nhm_...")
    .timeout(Duration.ofSeconds(30))
    .build();
```

### Endpoints

```java
ListResponse<Endpoint> endpoints = mgmt.endpoints().list("ws_abc");

Endpoint endpoint = mgmt.endpoints().create("ws_abc", new CreateEndpointOptions()
    .url("https://example.com/webhooks")
    .description("Production webhook")
    .type("webhook") // "webhook" | "slack"
    .metadata(Map.of("team", "payments")));

Endpoint endpoint = mgmt.endpoints().get("ws_abc", "ep_123");

mgmt.endpoints().update("ws_abc", "ep_123", new UpdateEndpointOptions()
    .description("Updated")
    .isActive(false));

mgmt.endpoints().delete("ws_abc", "ep_123");
```

### Event Types

```java
ListResponse<EventType> eventTypes = mgmt.eventTypes().list("ws_abc");

EventType eventType = mgmt.eventTypes().create("ws_abc", new CreateEventTypeOptions()
    .name("order.paid")
    .description("Fired when an order is paid"));

EventType eventType = mgmt.eventTypes().get("ws_abc", "evt_123");

mgmt.eventTypes().update("ws_abc", "evt_123", new UpdateEventTypeOptions()
    .description("Updated description"));

mgmt.eventTypes().delete("ws_abc", "evt_123");
```

### Applications

```java
ListResponse<Application> apps = mgmt.applications().list("ws_abc");

Application app = mgmt.applications().create("ws_abc", new CreateApplicationOptions()
    .name("Acme Corp")
    .externalId("acme-123")
    .metadata(Map.of("tier", "pro")));

Application app = mgmt.applications().get("ws_abc", "app_123");

mgmt.applications().update("ws_abc", "app_123", new UpdateApplicationOptions()
    .name("Acme Inc"));

mgmt.applications().delete("ws_abc", "app_123");

// Endpoints scoped to an application
ListResponse<Endpoint> endpoints = mgmt.applications().listEndpoints("ws_abc", "app_123");
Endpoint ep = mgmt.applications().createEndpoint("ws_abc", "app_123", new CreateEndpointOptions()
    .url("https://acme.com/webhooks"));
```

### Subscriptions

```java
ListResponse<Subscription> subs = mgmt.subscriptions().list("ws_abc", "ep_123");

mgmt.subscriptions().create("ws_abc", "ep_123", new CreateSubscriptionOptions()
    .eventTypeIds(List.of("evt_456")));

mgmt.subscriptions().delete("ws_abc", "ep_123", "evt_456");
```

### Environments

```java
import com.nahook.types.CreateEnvironmentOptions;
import com.nahook.types.UpdateEnvironmentOptions;

ListResponse<Environment> envs = mgmt.environments().list("ws_abc");

Environment env = mgmt.environments().create("ws_abc",
    new CreateEnvironmentOptions("Staging", "staging"));

Environment env = mgmt.environments().get("ws_abc", "env_123");

mgmt.environments().update("ws_abc", "env_123",
    UpdateEnvironmentOptions.withName("Pre-production"));

mgmt.environments().delete("ws_abc", "env_123");
```

### Event Type Visibility

Control which event types are visible per environment.

```java
import com.nahook.types.SetVisibilityOptions;

ListResponse<EventTypeVisibility> vis = mgmt.environments()
    .listEventTypeVisibility("ws_abc", "env_123");

EventTypeVisibility result = mgmt.environments()
    .setEventTypeVisibility("ws_abc", "env_123", "evt_456",
        new SetVisibilityOptions(true));
// result.getEventTypeId()   -> "evt_456"
// result.getEventTypeName() -> "order.paid"
// result.isPublished()      -> true
```

### Portal Sessions

```java
PortalSession session = mgmt.portalSessions().create("ws_abc", "app_123",
    new CreatePortalSessionOptions()
        .metadata(Map.of("userId", "user-456")));
// session.getUrl()       -> redirect end-user here
// session.getCode()      -> one-time exchange code
// session.getExpiresAt() -> expiration timestamp
```

---

## Error Handling

All SDK errors extend `NahookException` (which extends `RuntimeException`). Three specific types cover every failure mode:

```java
import com.nahook.errors.NahookApiException;
import com.nahook.errors.NahookNetworkException;
import com.nahook.errors.NahookTimeoutException;

try {
    client.send("ep_abc", new SendOptions(payload));
} catch (NahookApiException e) {
    // API returned an error response
    System.out.println(e.getStatus());      // 404
    System.out.println(e.getCode());        // "not_found"
    System.out.println(e.getMessage());     // "Endpoint not found"
    System.out.println(e.getRetryAfter());  // seconds (on 429s)

    // Convenience checks
    e.isRetryable();       // true for 5xx, 429
    e.isAuthError();       // true for 401, 403 (token_disabled)
    e.isNotFound();        // true for 404
    e.isRateLimited();     // true for 429
    e.isValidationError(); // true for 400
} catch (NahookNetworkException e) {
    System.out.println(e.getCause()); // original I/O exception
} catch (NahookTimeoutException e) {
    System.out.println(e.getMessage()); // timeout that was exceeded
}
```

---

## Authentication

| Token type | Format | Use with |
|------------|--------|----------|
| API key | `nhk_{region}_{hex}` | `NahookClient` (ingestion) |
| Management token | `nhm_{hex}` | `NahookManagement` (CRUD) |

The region prefix in API keys (`us`, `eu`, `ap`) enables automatic routing to the nearest regional API. You do not need to configure `baseUrl` manually when using region-prefixed keys.

---

## License

MIT
