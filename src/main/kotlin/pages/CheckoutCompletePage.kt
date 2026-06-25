package pages

import com.microsoft.playwright.Page

class CheckoutCompletePage(page: Page) : BasePage(page) {
    override val url = "/checkout-complete.html"

    private val completeHeader = "[data-test='complete-header']"
    private val backHomeButton = "[data-test='back-to-products']"

    fun shouldBeLoaded(): CheckoutCompletePage {
        shouldHaveUrl("checkout-complete")
        shouldBeVisible(completeHeader)
        return this
    }

    fun shouldShowOrderConfirmation() {
        shouldBeLoaded()
        shouldHaveText(completeHeader, "Thank you for your order!")
    }

    fun backToProducts(): InventoryPage {
        click(backHomeButton)
        return InventoryPage(page).shouldBeLoaded()
    }
}
