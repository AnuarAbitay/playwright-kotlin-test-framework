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
    fun productCount(page: Page) {
        loginAsStandardUser(page)
            .shouldHaveProductCount(6)
    }

    @Test
    @DisplayName("Add product to cart updates badge count")
    fun addToCart(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .shouldHaveCartBadge(1)
    }

    @Test
    @DisplayName("Add multiple products and verify badge count")
    fun addMultipleToCart(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .addProductToCartByName("Sauce Labs Bike Light")
            .addProductToCartByName("Sauce Labs Onesie")
            .shouldHaveCartBadge(3)
    }

    @Test
    @DisplayName("Remove product from cart decreases badge count")
    fun removeFromCart(page: Page) {
        loginAsStandardUser(page)
            .addProductToCartByName("Sauce Labs Backpack")
            .addProductToCartByName("Sauce Labs Bike Light")
            .removeProductByName("Sauce Labs Backpack")
            .shouldHaveCartBadge(1)
    }

    @Test
    @DisplayName("Sort products by price low to high")
    fun sortByPriceAsc(page: Page) {
        loginAsStandardUser(page)
            .sortBy("lohi")
            .shouldHavePricesSortedAsc()
    }

    @Test
    @DisplayName("Sort products by name Z to A")
    fun sortByNameDesc(page: Page) {
        loginAsStandardUser(page)
            .sortBy("za")
            .shouldHaveNamesSortedDesc()
    }
}