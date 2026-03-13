package extensions

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import config.TestConfig
import context.PageHolder
import context.PlaywrightHolder
import data.enums.BrowserEngine.*
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.junit.jupiter.api.extension.*
import java.util.Optional

class PlaywrightExtension : BeforeAllCallback, BeforeEachCallback, ParameterResolver, TestWatcher {
    private val logger = logger(this::class.java.name)

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create(PlaywrightExtension::class.java)
        private const val BROWSER_KEY = "playwright_browser"
        private const val PAGE_KEY = "playwright_page"
    }

    /**
     * Один раз на класс: создаём Playwright + Browser.
     * Кладём в class-level Store → close() в конце класса.
     */
    override fun beforeAll(context: ExtensionContext) {
        val config = TestConfig
        val playwright = Playwright.create()

        val launchOptions = BrowserType.LaunchOptions()
            .setHeadless(config.headless)

        val browser = when (config.browserType) {
            CHROMIUM -> playwright.chromium().launch(launchOptions)
            FIREFOX -> playwright.firefox().launch(launchOptions)
            WEBKIT -> playwright.webkit().launch(launchOptions)
        }

        context.getStore(NAMESPACE).put(BROWSER_KEY, PlaywrightHolder(playwright, browser))
        logger.info { "Browser created for class: ${context.displayName} [${config.browserType}]" }
    }

    override fun beforeEach(context: ExtensionContext) {
        val browser = getBrowserHolder(context).browser
        val browserContext = browser.newContext()
        val page = browserContext.newPage()

        context.getStore(NAMESPACE).put(PAGE_KEY, PageHolder(browserContext, page))
    }

    // --- TestWatcher ---

    override fun testFailed(context: ExtensionContext, cause: Throwable?) {
        logger.warn { "Test failed: ${context.displayName} — ${cause?.message}" }
    }

    override fun testSuccessful(context: ExtensionContext) {}
    override fun testAborted(context: ExtensionContext, cause: Throwable?) {}
    override fun testDisabled(context: ExtensionContext, reason: Optional<String>?) {}

    // --- ParameterResolver ---

    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean = parameterContext.parameter.type in supportedTypes

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any = when (parameterContext.parameter.type) {
        com.microsoft.playwright.Page::class.java -> getPageHolder(extensionContext).page
        com.microsoft.playwright.BrowserContext::class.java -> getPageHolder(extensionContext).context
        com.microsoft.playwright.Browser::class.java -> getBrowserHolder(extensionContext).browser
        else -> throw ParameterResolutionException("Unsupported: ${parameterContext.parameter.type}")
    }

    // --- Helpers ---

    private fun getBrowserHolder(context: ExtensionContext): PlaywrightHolder =
        context.parent.get().getStore(NAMESPACE).get(BROWSER_KEY, PlaywrightHolder::class.java)

    private fun getPageHolder(context: ExtensionContext): PageHolder =
        context.getStore(NAMESPACE).get(PAGE_KEY, PageHolder::class.java)

    private val supportedTypes = setOf(
        com.microsoft.playwright.Page::class.java,
        com.microsoft.playwright.BrowserContext::class.java,
        com.microsoft.playwright.Browser::class.java
    )
}