package pages

import com.microsoft.playwright.Page

class LoginPage(page: Page) : BasePage(page) {
    override val url = "https://www.saucedemo.com/"

    private val usernameInput = "[data-test='username']"
    private val passwordInput = "[data-test='password']"
    private val loginButton = "[data-test='login-button']"
    private val errorMessage = "[data-test='error']"

    fun open(): LoginPage {
        page.navigate(url)
        page.waitForLoadState()
        return this
    }

    fun login(username: String, password: String): InventoryPage {
        fill(usernameInput, username)
        fill(passwordInput, password)
        click(loginButton)
        return InventoryPage(page)
    }

    fun loginExpectingError(username: String, password: String): LoginPage {
        fill(usernameInput, username)
        fill(passwordInput, password)
        click(loginButton)
        return this
    }

    fun shouldShowError(expectedText: String) {
        shouldContainText(errorMessage, expectedText)
    }
}