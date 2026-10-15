package e2e.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;

import e2e.base.BaseTest;
import e2e.pages.DashboardPage;
import e2e.pages.LoginPage;

class LoginE2ETest extends BaseTest {

    private static final String VALID_USER = env("UTC_USER");
    private static final String VALID_PASS = env("UTC_PASS");

    private static final String MSG_NO_USERNAME = "Bạn chưa nhập tên đăng nhập";
    private static final String MSG_NO_PASSWORD = "Bạn chưa nhập mật khẩu";
    private static final String MSG_BAD_CREDENTIALS = "Tài khoản hoặc mật khẩu không đúng.";

    private LoginPage loginPage;

    private static String env(String name) {
        String v = System.getenv(name);
        return v == null ? "" : v;
    }

    private static void requireCredentials() {
        assumeTrue(!VALID_USER.isBlank() && !VALID_PASS.isBlank(),
                "Cần đặt biến môi trường UTC_USER và UTC_PASS (tài khoản kiểm thử hợp lệ)");
    }

    @BeforeEach
    void openLogin() {
        loginPage = new LoginPage(driver, BASE_URL).open();
    }

    // ===================== TC1 - TC16 (theo bảng test case đã có) =====================

    @Test
    @DisplayName("TC1 - Đăng nhập thành công")
    void tc01_loginSuccess() {
        requireCredentials();
        loginPage.login(VALID_USER, VALID_PASS);
        assertTrue(new DashboardPage(driver).isLoaded(), "Phải rời trang đăng nhập sau khi đăng nhập hợp lệ");
        assertFalse(loginPage.getCurrentUrl().contains("/Login"), "URL không còn là trang Login");
    }

    @Test
    @DisplayName("TC2 - Để trống cả username và password")
    void tc02_emptyBoth() {
        loginPage.clickLogin();
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_NO_USERNAME, loginPage.getErrorMessage());
        assertTrue(loginPage.isOnLoginPage());
    }

    private Set<Cookie> persistentCookies() {
        return driver.manage().getCookies().stream()
                .filter(c -> c.getExpiry() != null)
                .collect(Collectors.toSet());
    }
}
