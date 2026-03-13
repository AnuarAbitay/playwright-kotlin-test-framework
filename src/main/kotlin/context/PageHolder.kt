package context

import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.lang.AutoCloseable

private val logger = logger {}

/**
 * Method-level: свежий на каждый тест.
 * Store вызовет close() когда тест завершится.
 */
class PageHolder(
    val context: BrowserContext,
    val page: Page
) : AutoCloseable {
    override fun close() {
        runCatching { page.close() }
            .onFailure { logger.warn(it) { "Failed to close Page" } }
        runCatching { context.close() }
            .onFailure { logger.warn(it) { "Failed to close Context" } }
        logger.debug { "Page + Context closed (method-level)" }
    }
}