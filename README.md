# Playwright Kotlin Test Framework

A lightweight, extensible Web UI test automation framework built with **Kotlin**, **Playwright**, and **JUnit 5**.

Designed for clean architecture, easy maintenance, and fast parallel execution.

## Tech Stack

| Tool | Purpose |
|------|---------|
| Kotlin 2.2 | Primary language |
| Playwright 1.55 | Browser automation |
| JUnit 5.11 | Test runner & lifecycle |
| Allure 2.29 | Test reporting |
| AssertJ 3.27 | Fluent assertions |
| Kotlin-logging + Logback | Structured logging |
| Gradle (Kotlin DSL) | Build system |
| Java 17 | JVM target |

## Architecture

```
src/
├── main/kotlin/
│   ├── config/
│   │   └── TestConfig.kt              # Centralized config via system properties
│   ├── context/
│   │   ├── PlaywrightHolder.kt        # Class-level: Playwright + Browser lifecycle
│   │   └── PageHolder.kt              # Method-level: BrowserContext + Page lifecycle
│   ├── data/
│   │   └── enums/
│   │       └── BrowserEngine.kt       # Supported browsers: Chromium, Firefox, WebKit
│   ├── extensions/
│   │   └── PlaywrightExtension.kt     # JUnit 5 extension — full lifecycle management
│   └── pages/
│       ├── BasePage.kt                # Base page with reusable actions & assertions
│       ├── LoginPage.kt              # Login page
│       ├── InventoryPage.kt          # Product catalog page
│       ├── CartPage.kt               # Shopping cart page
│       ├── CheckoutStepOnePage.kt    # Checkout form page
│       ├── CheckoutStepTwoPage.kt    # Checkout overview page
│       └── CheckoutCompletePage.kt   # Order confirmation page
└── test/
    ├── kotlin/
    │   ├── LoginTest.kt               # Login scenarios (4 tests)
    │   ├── InventoryTest.kt           # Catalog & cart scenarios (8 tests)
    │   └── CheckoutTest.kt            # E2E checkout scenarios (5 tests)
    └── resources/
        ├── META-INF/services/
        │   └── ...Extension            # Auto-registration via ServiceLoader
        ├── allure.properties           # Allure results directory config
        └── junit-platform.properties   # Enables extension auto-detection
```

## Key Design Decisions

### Global Extension Auto-Detection

The framework uses JUnit 5's **ServiceLoader** mechanism instead of `@ExtendWith` annotations:

- `junit-platform.properties` enables `junit.jupiter.extensions.autodetection.enabled = true`
- `META-INF/services/org.junit.jupiter.api.extension.Extension` registers `PlaywrightExtension` globally

Every test class automatically gets browser lifecycle management — no boilerplate annotations needed.

### Two-Level Lifecycle Management

`PlaywrightExtension` implements `BeforeAllCallback`, `BeforeEachCallback`, `AfterEachCallback`, `ParameterResolver`, and `TestWatcher`:

- **Class-level** (`PlaywrightHolder`) — Playwright instance + Browser created once per test class, stored in JUnit's `ExtensionContext.Store`, auto-closed via `AutoCloseable`
- **Method-level** (`PageHolder`) — fresh `BrowserContext` + `Page` for each test, ensuring full isolation. Explicitly closed in `afterEach` for immediate resource cleanup
- **Parameter injection** — tests receive `Page`, `BrowserContext`, or `Browser` directly as method parameters
- **Screenshot on failure** — automatic full-page screenshot attached to Allure report when a test fails

### Page Object Pattern with Fluent Assertions

All assertions are encapsulated inside Page Objects — tests contain zero `assertThat` calls:

```kotlin
loginAsStandardUser(page)
    .addProductToCartByName("Sauce Labs Backpack")
    .goToCart()
    .shouldHaveItemCount(1)
    .checkout()
    .fillForm("John", "Doe", "12345")
    .continueToOverview()
    .finish()
    .shouldShowOrderConfirmation()
```

`BasePage` provides a clean DSL for common operations: navigation, locators (CSS, text, ARIA roles, test IDs), actions, waits, and assertions.

### Configurable via System Properties

| Property | Default | Options |
|----------|---------|---------|
| `browser` | `CHROMIUM` | `CHROMIUM`, `FIREFOX`, `WEBKIT` |
| `headless` | `true` | `true`, `false` |

## Getting Started

### Prerequisites

- JDK 17+
- Gradle 8+

### Run Tests

```bash
# Default: Chromium, headless mode
./gradlew clean test

# Headed mode (see the browser)
./gradlew test -Dheadless=false

# Firefox
./gradlew test -Dbrowser=FIREFOX

# WebKit in headed mode
./gradlew test -Dbrowser=WEBKIT -Dheadless=false
```

### Allure Report

```bash
# Run tests and generate report
./gradlew clean test

# Open report in browser
./gradlew allureServe
```

### Test Coverage

| Suite | Tests | Scenarios |
|-------|-------|-----------|
| LoginTest | 4 | Valid login, locked user, invalid credentials, empty credentials |
| InventoryTest | 8 | Product count, add/remove cart, badge count, sorting (price, name), logout |
| CheckoutTest | 5 | Single/multi product checkout, form validation, cart management, navigation |

## Roadmap

- [x] Allure reporting integration
- [x] Screenshot on failure
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Environment-based config (dev / staging / prod)
- [ ] API layer for backend testing
- [ ] Docker support for parallel execution

## Author

**Anuar Abitay** — Senior QA Automation Engineer

- [GitHub](https://github.com/AnuarAbitay)
- [LinkedIn](https://www.linkedin.com/in/anuar-abitay-automationqa)
