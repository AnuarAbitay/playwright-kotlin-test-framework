package context

import com.microsoft.playwright.Browser
import com.microsoft.playwright.Playwright
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.lang.AutoCloseable

private val logger = logger {}

/**
 * Class-level: живёт весь тестовый класс.
 * Store вызовет close() когда класс завершится.
 */
class PlaywrightHolder(
    val playwright: Playwright,
    val browser: Browser
) : AutoCloseable {
    override fun close() {
        runCatching { browser.close() }
            .onFailure { logger.warn(it) { "Failed to close browser" } }
        runCatching { playwright.close() }
            .onFailure { logger.warn(it) { "Failed to close playwright" } }
        logger.debug { "PlaywrightHolder closed (class-level)" }
    }
}