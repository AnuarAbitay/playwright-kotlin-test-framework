import com.microsoft.playwright.Page
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import pages.LoginPage

@DisplayName("Login Tests")
class LoginTest {

    @Test
    @DisplayName("Successful login with standard user")
    fun successfulLogin(page: Page) {
        LoginPage(page)
            .open()
            .login("standard_user", "secret_sauce")
            .shouldBeLoaded()
    }

    @Test
    @DisplayName("Login fails with locked out user")
    fun lockedOutUser(page: Page) {
        LoginPage(page)
            .open()
            .loginExpectingError("locked_out_user", "secret_sauce")
            .shouldShowError("Sorry, this user has been locked out")
    }

    @Test
    @DisplayName("Login fails with invalid credentials")
    fun invalidCredentials(page: Page) {
        LoginPage(page)
            .open()
            .loginExpectingError("invalid_user", "wrong_password")
            .shouldShowError("Username and password do not match")
    }

    @Test
    @DisplayName("Login fails with empty credentials")
    fun emptyCredentials(page: Page) {
        LoginPage(page)
            .open()
            .loginExpectingError("", "")
            .shouldShowError("Username is required")
    }
}