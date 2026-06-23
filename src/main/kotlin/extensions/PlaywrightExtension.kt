package extensions

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.Page.ScreenshotOptions
import config.TestConfig
import context.PageHolder
import context.PlaywrightHolder
import data.enums.BrowserEngine.CHROMIUM
import data.enums.BrowserEngine.FIREFOX
import data.enums.BrowserEngine.WEBKIT
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.qameta.allure.Allure.addAttachment
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver

class PlaywrightExtension : BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver, AfterTestExecutionCallback {
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

        try {
            val launchOptions = BrowserType.LaunchOptions()
                .setHeadless(config.headless)

            val browser = when (config.browserType) {
                CHROMIUM -> playwright.chromium().launch(launchOptions)
                FIREFOX -> playwright.firefox().launch(launchOptions)
                WEBKIT -> playwright.webkit().launch(launchOptions)
            }

            context.getStore(NAMESPACE).put(
                BROWSER_KEY,
                PlaywrightHolder(playwright, browser)
            )

            logger.info {
                "Browser created for class: ${context.displayName} [${config.browserType}]"
            }
        } catch (exception: Exception) {
            runCatching { playwright.close() }
            throw exception
        }
    }

    override fun beforeEach(context: ExtensionContext) {
        val browser = getBrowserHolder(context).browser
        val browserContext = browser.newContext()
        val page = browserContext.newPage()

        context.getStore(NAMESPACE).put(PAGE_KEY, PageHolder(browserContext, page))
    }

    override fun afterTestExecution(context: ExtensionContext) {
        if (context.executionException.isEmpty) {
            return
        }

        val pageHolder = context.getStore(NAMESPACE)
            .get(PAGE_KEY, PageHolder::class.java)
            ?: return

        runCatching {
            val screenshot = pageHolder.page.screenshot(
                ScreenshotOptions().setFullPage(true)
            )

            addAttachment(
                "Screenshot on failure",
                "image/png",
                screenshot.inputStream(),
                "png"
            )
        }.onFailure { exception ->
            logger.error(exception) {
                "Failed to capture screenshot for: ${context.displayName}"
            }
        }
    }

    override fun afterEach(context: ExtensionContext) {
        val pageHolder = context.getStore(NAMESPACE).remove(PAGE_KEY, PageHolder::class.java)
        pageHolder?.close()
    }

    // --- ParameterResolver ---

    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean = parameterContext.parameter.type in supportedTypes

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any = when (parameterContext.parameter.type) {
        Page::class.java -> getPageHolder(extensionContext).page
        BrowserContext::class.java -> getPageHolder(extensionContext).context
        Browser::class.java -> getBrowserHolder(extensionContext).browser
        else -> throw ParameterResolutionException("Unsupported: ${parameterContext.parameter.type}")
    }

    // --- Helpers ---

    private fun getBrowserHolder(context: ExtensionContext): PlaywrightHolder {
        val classContext = context.parent.orElseThrow {
            IllegalStateException("Class ExtensionContext was not found")
        }

        return classContext.getStore(NAMESPACE)
            .get(BROWSER_KEY, PlaywrightHolder::class.java)
            ?: throw IllegalStateException("PlaywrightHolder was not initialized")
    }

    private fun getPageHolder(context: ExtensionContext): PageHolder {
        return context.getStore(NAMESPACE)
            .get(PAGE_KEY, PageHolder::class.java)
            ?: throw IllegalStateException("PageHolder was not initialized")
    }

    private val supportedTypes = setOf(
        Page::class.java,
        BrowserContext::class.java,
        Browser::class.java
    )
}