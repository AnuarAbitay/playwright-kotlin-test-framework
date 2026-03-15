import com.microsoft.playwright.Page
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import pages.LoginPage

@DisplayName("Checkout Tests")
class CheckoutTest {

    private fun loginAsStandardUser(page: Page) =
        LoginPage(page)
            .open()
            .login("standard_user", "secret_sauce")

    @Test
    @DisplayName("Complete checkout for a single product")
    fun `complete checkout for a single product`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .goToCart()
            .shouldHaveItemCount(1)
            .checkout()
            .fillForm("John", "Doe", "12345")
            .continueToOverview()
            .finish()
            .shouldShowOrderConfirmation()
    }

    @Test
    @DisplayName("Complete checkout for multiple products")
    fun `complete checkout for multiple products`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .addProductToCartByName("Sauce Labs Onesie")
            .goToCart()
            .shouldHaveItemCount(2)
            .checkout()
            .fillForm("Jane", "Smith", "54321")
            .continueToOverview()
            .finish()
            .shouldShowOrderConfirmation()
    }

    @Test
    @DisplayName("Checkout fails without filling form")
    fun `checkout fails without filling form`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Bike Light")
            .goToCart()
            .checkout()
            .continueExpectingError()
            .shouldShowError("First Name is required")
    }

    @Test
    @DisplayName("Remove item from cart before checkout")
    fun `remove item from cart before checkout`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .addProductToCartByName("Sauce Labs Bike Light")
            .goToCart()
            .removeItemByName("Sauce Labs Backpack")
            .shouldHaveItemCount(1)
            .shouldContainProduct("Sauce Labs Bike Light")
            .shouldNotContainProduct("Sauce Labs Backpack")
    }

    @Test
    @DisplayName("Back to products from checkout complete")
    fun `back to products from checkout complete`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Onesie")
            .goToCart()
            .checkout()
            .fillForm("Test", "User", "90210")
            .continueToOverview()
            .finish()
            .backToProducts()
            .shouldBeLoaded()
    }
}
