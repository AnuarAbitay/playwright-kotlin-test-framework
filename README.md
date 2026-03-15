# Playwright Kotlin Test Framework

A lightweight, extensible Web UI test automation framework built with **Kotlin**, **Playwright**, and **JUnit 5**.

Designed for clean architecture, easy maintenance, and fast parallel execution.

## Tech Stack

| Tool | Purpose |
|------|---------|
| Kotlin 2.2 | Primary language |
| Playwright 1.55 | Browser automation |
| JUnit 5.13 | Test runner & lifecycle |
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
│       └── LoginPage.kt              # Example page object
└── test/resources/
    ├── META-INF/services/
    │   └── ...Extension               # Auto-registration via ServiceLoader
    └── junit-platform.properties      # Enables extension auto-detection
```

## Key Design Decisions

### Global Extension Auto-Detection

The framework uses JUnit 5's **ServiceLoader** mechanism instead of `@ExtendWith` annotations:

- `junit-platform.properties` enables `junit.jupiter.extensions.autodetection.enabled = true`
- `META-INF/services/org.junit.jupiter.api.extension.Extension` registers `PlaywrightExtension` globally

This means **every test class automatically gets browser lifecycle management** — no boilerplate annotations needed.

### Two-Level Lifecycle Management

`PlaywrightExtension` implements `BeforeAllCallback`, `BeforeEachCallback`, `ParameterResolver`, and `TestWatcher`:

- **Class-level** (`PlaywrightHolder`) — Playwright instance + Browser created once per test class, stored in JUnit's `ExtensionContext.Store`, auto-closed via `AutoCloseable`
- **Method-level** (`PageHolder`) — fresh `BrowserContext` + `Page` for each test, ensuring full isolation
- **Parameter injection** — tests receive `Page`, `BrowserContext`, or `Browser` directly as method parameters

### Page Object Pattern

`BasePage` provides a clean DSL for common operations:

- **Navigation**: `open()`, `currentUrl()`, `title()`
- **Locators**: CSS selectors, text, ARIA roles, test IDs
- **Actions**: `click()`, `fill()`, `getText()`, `isVisible()`
- **Waits**: `waitForVisible()`, `waitForHidden()`, `waitForUrl()`
- **Assertions**: `shouldBeVisible()`, `shouldHaveText()`, `shouldContainText()`, `shouldHaveUrl()`

### Configurable via System Properties

| Property | Default | Options |
|----------|---------|---------|
| `browser` | `CHROMIUM` | `CHROMIUM`, `FIREFOX`, `WEBKIT` |
| `headless` | `false` | `true`, `false` |

## Getting Started

### Prerequisites

- JDK 17+
- Gradle 8+

### Run Tests

```bash
# Default: Chromium, headed mode
./gradlew test

# Firefox in headless mode
./gradlew test -Dbrowser=FIREFOX -Dheadless=true

# WebKit
./gradlew test -Dbrowser=WEBKIT
```

### Create a New Page Object

```kotlin
class DashboardPage(page: Page) : BasePage(page) {
    override val url = "https://example.com/dashboard"

    private val welcomeMessage = "[data-testid='welcome']"

    fun verifyWelcome(username: String) {
        shouldContainText(welcomeMessage, username)
    }
}
```

### Write a Test

No `@ExtendWith` needed — the extension is auto-detected globally:

```kotlin
class LoginTest {

    @Test
    fun `user can log in successfully`(page: Page) {
        val loginPage = LoginPage(page).open()
        // ... test steps
    }
}
```

## Roadmap

- [ ] Allure reporting integration
- [ ] CI/CD pipeline (GitLab CI / GitHub Actions)
- [ ] Screenshot on failure
- [ ] Environment-based config (dev / staging / prod)
- [ ] API layer for backend testing
- [ ] Docker support for parallel execution

## Author

**Anuar Abitay** — Senior QA Automation Engineer

- [GitHub](https://github.com/AnuarAbitay)
- [LinkedIn](https://linkedin.com/in/anuar-abitay-automationqa)
