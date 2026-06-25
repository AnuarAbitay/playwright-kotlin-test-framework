package config

import data.enums.BrowserEngine
import data.enums.BrowserEngine.valueOf

object TestConfig {
    val headless: Boolean
        get() = System.getProperty("headless", "true").toBoolean()

    val browserType: BrowserEngine
        get() = valueOf(
            System.getProperty("browser", "CHROMIUM").uppercase()
        )

    val baseUrl: String
        get() = System.getProperty("baseUrl", "https://www.saucedemo.com").trimEnd('/')
}