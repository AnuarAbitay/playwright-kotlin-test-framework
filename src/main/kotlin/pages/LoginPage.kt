package pages

import com.microsoft.playwright.Page

class LoginPage(page: Page) : BasePage(page) {
    override val url = "https://example.com/login"
}