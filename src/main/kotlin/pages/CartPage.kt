package pages

import com.microsoft.playwright.Locator.FilterOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat

class CartPage(page: Page) : BasePage(page) {
    override val url = "https://www.saucedemo.com/cart.html"

    private val cartItems = "[data-test='inventory-item']"
    private val checkoutButton = "[data-test='checkout']"

    fun removeItemByName(productName: String): CartPage {
        val item = page.locator(cartItems)
            .filter(FilterOptions().setHasText(productName))
        item.locator("button:has-text('Remove')").click()
        return this
    }

    fun checkout(): CheckoutStepOnePage {
        click(checkoutButton)
        return CheckoutStepOnePage(page)
    }

    fun shouldHaveItemCount(expected: Int): CartPage {
        assertThat(page.locator(cartItems)).hasCount(expected)
        return this
    }

    fun shouldContainProduct(productName: String): CartPage {
        val item = page.locator(cartItems)
            .filter(FilterOptions().setHasText(productName))
        assertThat(item).hasCount(1)
        return this
    }

    fun shouldNotContainProduct(productName: String): CartPage {
        val item = page.locator(cartItems)
            .filter(FilterOptions().setHasText(productName))
        assertThat(item).hasCount(0)
        return this
    }
}
