# Playwright Kotlin Test Framework

A lightweight Web UI test automation framework built with **Kotlin**, **Playwright**, **JUnit 5**, and **Allure**.

The project demonstrates Page Object architecture, isolated browser contexts, JUnit 5 lifecycle management, parameter injection, and automatic screenshots on test failure.

## Tech Stack

| Tool                 | Version | Purpose                              |
|----------------------|--------:|--------------------------------------|
| Kotlin               |  2.3.20 | Primary programming language         |
| Java                 |      24 | JVM toolchain                        |
| Playwright           |  1.55.0 | Browser automation                   |
| JUnit                |  5.14.4 | Test runner and lifecycle management |
| Allure Java          |  2.35.2 | Test reporting integration           |
| Allure Gradle Plugin |   4.0.2 | Report generation                    |
| AssertJ              |  3.27.7 | Fluent assertions                    |
| Kotlin Logging       |   7.0.3 | Logging facade                       |
| Logback              |  1.5.13 | Logging implementation               |
| Gradle               |   9.5.1 | Build system with Kotlin DSL         |

## Tested Application

The tests cover the public demo application:

```text
https://www.saucedemo.com
```

## Project Structure

```text
src/
├── main/kotlin/
│   ├── config/
│   │   └── TestConfig.kt
│   ├── context/
│   │   ├── PageHolder.kt
│   │   └── PlaywrightHolder.kt
│   ├── data/enums/
│   │   └── BrowserEngine.kt
│   ├── extensions/
│   │   └── PlaywrightExtension.kt
│   └── pages/
│       ├── BasePage.kt
│       ├── CartPage.kt
│       ├── CheckoutCompletePage.kt
│       ├── CheckoutStepOnePage.kt
│       ├── CheckoutStepTwoPage.kt
│       ├── InventoryPage.kt
│       └── LoginPage.kt
└── test/
    ├── kotlin/
    │   ├── CheckoutTest.kt
    │   ├── InventoryTest.kt
    │   └── LoginTest.kt
    └── resources/
        ├── META-INF/services/
        │   └── org.junit.jupiter.api.extension.Extension
        ├── allure.properties
        └── junit-platform.properties
```

## Architecture

### Page Object Model

Page-specific actions and assertions are encapsulated inside Page Object classes. Tests describe user scenarios without directly working with selectors.

```kotlin
LoginPage(page)
    .open()
    .login("standard_user", "secret_sauce")
    .addProductToCartByName("Sauce Labs Backpack")
    .goToCart()
    .shouldHaveItemCount(1)
```

### JUnit 5 Extension

`PlaywrightExtension` manages the Playwright lifecycle and is registered globally through JUnit ServiceLoader.

The extension provides:

* one `Playwright` and `Browser` instance per test class;
* a new `BrowserContext` and `Page` for every test;
* automatic injection of `Page`, `BrowserContext`, and `Browser` into test methods;
* automatic resource cleanup;
* a full-page screenshot attached to Allure when a test fails.

### Test Isolation

Every test receives a separate `BrowserContext`. Cookies, local storage, and session state are therefore isolated between test cases.

## Configuration

The framework is configured through JVM system properties.

| Property   | Gradle default              | Supported values                |
|------------|-----------------------------|---------------------------------|
| `browser`  | `CHROMIUM`                  | `CHROMIUM`, `FIREFOX`, `WEBKIT` |
| `headless` | `true`                      | `true`, `false`                 |
| `baseUrl`  | `https://www.saucedemo.com` | Any valid application URL       |

## Prerequisites

* JDK 24
* Git

The project includes Gradle Wrapper, so a separate Gradle installation is not required.

## Running Tests

### Run all tests

```bash
./gradlew clean test
```

### Run in headed mode

```bash
./gradlew clean test -Dheadless=false
```

### Run in Firefox

```bash
./gradlew clean test -Dbrowser=FIREFOX
```

### Run in WebKit

```bash
./gradlew clean test -Dbrowser=WEBKIT
```

### Run against another environment

```bash
./gradlew clean test -DbaseUrl=https://example.com
```

### Combine parameters

```bash
./gradlew clean test \
  -Dbrowser=FIREFOX \
  -Dheadless=false \
  -DbaseUrl=https://www.saucedemo.com
```

### Run a single test class

```bash
./gradlew test --tests LoginTest
```

### Run a single test method

```bash
./gradlew test --tests LoginTest.successfulLogin
```

## Allure Report

Test results are stored in:

```text
build/allure-results
```

Run the tests and open the Allure report:

```bash
./gradlew clean test allureServe
```

Generate the report without opening it:

```bash
./gradlew allureReport
```

When a test fails, the framework automatically attaches a full-page screenshot to the report.

## Test Coverage

The project currently contains **15 UI tests**.

| Test suite      | Tests | Covered scenarios                                                                                       |
|-----------------|------:|---------------------------------------------------------------------------------------------------------|
| `LoginTest`     |     4 | Successful login, locked user, invalid credentials, empty credentials                                   |
| `InventoryTest` |     6 | Product count, adding and removing products, cart badge, price sorting, name sorting                    |
| `CheckoutTest`  |     5 | Single-product checkout, multi-product checkout, form validation, cart item removal, return to products |

## Supported Browsers

* Chromium
* Firefox
* WebKit

Tests run sequentially by default. Each test method receives an isolated browser context and page.

## Logging

The project uses Kotlin Logging with Logback. Browser lifecycle events and resource cleanup failures are written to the test logs.

## Author

**Anuar Abitay**
Senior QA Automation Engineer

* GitHub: [AnuarAbitay](https://github.com/AnuarAbitay)
* LinkedIn: [anuar-abitay-automationqa](https://www.linkedin.com/in/anuar-abitay-automationqa)
