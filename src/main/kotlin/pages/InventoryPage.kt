package pages

import com.microsoft.playwright.Locator.FilterOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat

class InventoryPage(page: Page) : BasePage(page) {
    override val url = "https://www.saucedemo.com/inventory.html"

    private val inventoryContainer = "[data-test='inventory-container']"
    private val inventoryItems = "[data-test='inventory-item']"
    private val cartBadge = "[data-test='shopping-cart-badge']"
    private val cartLink = "[data-test='shopping-cart-link']"
    private val sortDropdown = "[data-test='product-sort-container']"

    fun shouldBeLoaded() {
        shouldBeVisible(inventoryContainer)
        shouldHaveUrl("inventory")
    }

    fun addProductToCartByName(productName: String): InventoryPage {
        val item = page.locator(inventoryItems)
            .filter(FilterOptions().setHasText(productName))
        item.locator("button:has-text('Add to cart')").click()
        return this
    }

    fun removeProductByName(productName: String): InventoryPage {
        val item = page.locator(inventoryItems)
            .filter(FilterOptions().setHasText(productName))
        item.locator("button:has-text('Remove')").click()
        return this
    }

    fun goToCart(): CartPage {
        click(cartLink)
        return CartPage(page)
    }

    fun sortBy(value: String): InventoryPage {
        page.locator(sortDropdown).selectOption(value)
        return this
    }

    fun getProductNames(): List<String> =
        page.locator("[data-test='inventory-item-name']").allTextContents()

    fun getProductPrices(): List<Double> =
        page.locator("[data-test='inventory-item-price']")
            .allTextContents()
            .map { it.removePrefix("$").toDouble() }

    fun shouldHaveProductCount(expected: Int): InventoryPage {
        assertThat(page.locator(inventoryItems)).hasCount(expected)
        return this
    }

    fun shouldHaveCartBadge(expected: Int): InventoryPage {
        shouldHaveText(cartBadge, expected.toString())
        return this
    }

    fun shouldHavePricesSortedAsc(): InventoryPage {
        val prices = getProductPrices()
        org.assertj.core.api.Assertions.assertThat(prices).isSorted()
        return this
    }

    fun shouldHaveNamesSortedDesc(): InventoryPage {
        val names = getProductNames()
        org.assertj.core.api.Assertions.assertThat(names)
            .isSortedAccordingTo(compareByDescending { it })
        return this
    }
}
