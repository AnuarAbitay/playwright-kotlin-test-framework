package pages

import com.microsoft.playwright.Page

class CheckoutStepTwoPage(page: Page) : BasePage(page) {
    override val url = "/checkout-step-two.html"

    private val finishButton = "[data-test='finish']"

    fun shouldBeLoaded(): CheckoutStepTwoPage {
        shouldHaveUrl("checkout-step-two")
        shouldBeVisible(finishButton)
        return this
    }

    fun finish(): CheckoutCompletePage {
        click(finishButton)
        return CheckoutCompletePage(page).shouldBeLoaded()
    }
}
