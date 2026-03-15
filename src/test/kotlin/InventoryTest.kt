import com.microsoft.playwright.Page
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import pages.LoginPage

@DisplayName("Inventory Tests")
class InventoryTest {

    private fun loginAsStandardUser(page: Page) =
        LoginPage(page)
            .open()
            .login("standard_user", "secret_sauce")

    @Test
    @DisplayName("Inventory page displays 6 products")
    fun `inventory page displays 6 products`(page: Page) {
        loginAsStandardUser(page)
            .shouldHaveProductCount(6)
    }

    @Test
    @DisplayName("Add product to cart updates badge count")
    fun `add product to cart updates badge count`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .shouldHaveCartBadge(1)
    }

    @Test
    @DisplayName("Add multiple products and verify badge count")
    fun `add multiple products and verify badge count`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .addProductToCartByName("Sauce Labs Bike Light")
            .addProductToCartByName("Sauce Labs Onesie")
            .shouldHaveCartBadge(3)
    }

    @Test
    @DisplayName("Remove product from cart decreases badge count")
    fun `remove product from cart decreases badge count`(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .addProductToCartByName("Sauce Labs Bike Light")
            .removeProductByName("Sauce Labs Backpack")
            .shouldHaveCartBadge(1)
    }

    @Test
    @DisplayName("Sort products by price low to high")
    fun `sort products by price low to high`(page: Page) {
        loginAsStandardUser(page)
            .sortBy("lohi")
            .shouldHavePricesSortedAsc()
    }

    @Test
    @DisplayName("Sort products by name Z to A")
    fun `sort products by name Z to A`(page: Page) {
        loginAsStandardUser(page)
            .sortBy("za")
            .shouldHaveNamesSortedDesc()
    }
}