package pages

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import com.microsoft.playwright.options.WaitForSelectorState.HIDDEN
import com.microsoft.playwright.options.WaitForSelectorState.VISIBLE
import java.util.regex.Pattern.compile

abstract class BasePage(protected val page: Page) {
    abstract val url: String

    // --- Navigation ---

    fun <T : BasePage> T.open(): T {
        page.navigate(url)
        page.waitForLoadState()
        return this
    }

    fun currentUrl(): String = page.url()

    fun title(): String = page.title()

    // --- Locators ---

    protected fun locator(selector: String): Locator =
        page.locator(selector)

    protected fun locatorByText(text: String): Locator =
        page.getByText(text)

    protected fun locatorByRole(role: AriaRole, name: String? = null): Locator =
        if (name != null) page.getByRole(role, Page.GetByRoleOptions().setName(name))
        else page.getByRole(role)

    protected fun locatorByTestId(testId: String): Locator =
        page.getByTestId(testId)

    // --- Actions ---

    protected fun click(selector: String) {
        locator(selector).click()
    }

    protected fun fill(selector: String, text: String) {
        locator(selector).fill(text)
    }

    protected fun getText(selector: String): String =
        locator(selector).textContent() ?: ""

    protected fun isVisible(selector: String): Boolean =
        locator(selector).isVisible

    // --- Waits ---

    protected fun waitForVisible(selector: String) {
        locator(selector).waitFor(Locator.WaitForOptions().setState(VISIBLE))
    }

    protected fun waitForHidden(selector: String) {
        locator(selector).waitFor(Locator.WaitForOptions().setState(HIDDEN))
    }

    protected fun waitForUrl(urlPart: String) {
        page.waitForURL("**/*$urlPart*")
    }

    // --- Assertions ---

    protected fun shouldBeVisible(selector: String) {
        assertThat(locator(selector)).isVisible()
    }

    protected fun shouldHaveText(selector: String, expected: String) {
        assertThat(locator(selector)).hasText(expected)
    }

    protected fun shouldContainText(selector: String, expected: String) {
        assertThat(locator(selector)).containsText(expected)
    }

    protected fun shouldHaveUrl(urlPart: String) {
        assertThat(page).hasURL(compile(".*$urlPart.*"))
    }
}