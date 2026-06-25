package pages

import com.microsoft.playwright.Page

class CheckoutStepOnePage(page: Page) : BasePage(page) {
    override val url = "/checkout-step-one.html"

    private val firstNameInput = "[data-test='firstName']"
    private val lastNameInput = "[data-test='lastName']"
    private val postalCodeInput = "[data-test='postalCode']"
    private val continueButton = "[data-test='continue']"
    private val errorMessage = "[data-test='error']"

    fun fillForm(firstName: String, lastName: String, postalCode: String): CheckoutStepOnePage {
        fill(firstNameInput, firstName)
        fill(lastNameInput, lastName)
        fill(postalCodeInput, postalCode)
        return this
    }

    fun continueToOverview(): CheckoutStepTwoPage {
        click(continueButton)
        return CheckoutStepTwoPage(page)
    }

    fun continueExpectingError(): CheckoutStepOnePage {
        click(continueButton)
        return this
    }

    fun shouldShowError(expectedText: String) {
        shouldContainText(errorMessage, expectedText)
    }
}
