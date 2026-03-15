package pages

import com.microsoft.playwright.Page

class CheckoutCompletePage(page: Page) : BasePage(page) {
    override val url = "https://www.saucedemo.com/checkout-complete.html"

    private val completeHeader = "[data-test='complete-header']"
    private val backHomeButton = "[data-test='back-to-products']"

    fun shouldShowOrderConfirmation() {
        shouldHaveText(completeHeader, "Thank you for your order!")
        shouldHaveUrl("checkout-complete")
    }

    fun backToProducts(): InventoryPage {
        click(backHomeButton)
        return InventoryPage(page)
    }
}
