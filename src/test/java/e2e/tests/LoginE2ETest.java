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

    @Test
    @DisplayName("TC3 - Để trống username")
    void tc03_emptyUsername() {
        loginPage.enterPassword("Abc@12345");
        loginPage.clickLogin();
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_NO_USERNAME, loginPage.getErrorMessage());
        assertTrue(loginPage.isOnLoginPage());
    }

    @Test
    @DisplayName("TC4 - Để trống password")
    void tc04_emptyPassword() {
        loginPage.enterUsername("someuser");
        loginPage.clickLogin();
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_NO_PASSWORD, loginPage.getErrorMessage());
        assertTrue(loginPage.isOnLoginPage());
    }

    @Test
    @DisplayName("TC5 - Nhập sai username")
    void tc05_wrongUsername() {
        loginPage.login("khong_ton_tai_" + System.currentTimeMillis(), "Abc@12345");
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_BAD_CREDENTIALS, loginPage.getErrorMessage());
        assertTrue(loginPage.isOnLoginPage());
    }

    @Test
    @DisplayName("TC6 - Nhập sai password")
    void tc06_wrongPassword() {
        requireCredentials();
        loginPage.login(VALID_USER, VALID_PASS + "_sai");
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_BAD_CREDENTIALS, loginPage.getErrorMessage());
        assertTrue(loginPage.isOnLoginPage());
    }

    @Test
    @DisplayName("TC7 - Sai cả username và password")
    void tc07_wrongBoth() {
        loginPage.login("sai_user_" + System.currentTimeMillis(), "sai_pass");
        loginPage.waitUntilErrorVisible();
        String message = loginPage.getErrorMessage();
        assertEquals(MSG_BAD_CREDENTIALS, message);
        // thông báo không được cho biết cụ thể trường nào sai
        assertFalse(message.toLowerCase().contains("không tồn tại"));
        assertTrue(loginPage.isOnLoginPage());
    }

    @Test
    @DisplayName("TC8 - Chọn 'Giữ tôi luôn đăng nhập' rồi đăng nhập")
    void tc08_keepLoggedInChecked() {
        requireCredentials();
        loginPage.clickPersistentCheckbox();
        assertTrue(loginPage.isPersistentChecked(), "Checkbox phải ở trạng thái được chọn");
        loginPage.login(VALID_USER, VALID_PASS);
        assertTrue(new DashboardPage(driver).isLoaded());
        assertTrue(persistentCookies().size() > 0,
                "Khi chọn ghi nhớ, hệ thống nên tạo cookie có thời hạn (persistent)");
    }

    @Test
    @DisplayName("TC9 - Không chọn 'Giữ tôi luôn đăng nhập'")
    void tc09_keepLoggedInUnchecked() {
        requireCredentials();
        assertFalse(loginPage.isPersistentChecked(), "Mặc định checkbox không được chọn");
        loginPage.login(VALID_USER, VALID_PASS);
        assertTrue(new DashboardPage(driver).isLoaded());
        assertEquals(0, persistentCookies().size(),
                "Không chọn ghi nhớ thì không nên có cookie xác thực có thời hạn");
    }

    @Test
    @DisplayName("TC10 - Đăng nhập bằng e-mail UTC")
    void tc10_loginWithUtcEmail() {
        assertTrue(loginPage.getEmailUtcHref().startsWith("https://accounts.google.com/"));
        loginPage.clickEmailUtcLink();
        loginPage.waitForUrlContains("accounts.google.com");
        assertTrue(loginPage.getCurrentUrl().contains("accounts.google.com"));
    }

    @Test
    @DisplayName("TC11 - Mở chức năng quên mật khẩu")
    void tc11_forgotPassword() {
        loginPage.clickForgotPassword();
        loginPage.waitForUrlContains("/Login/GetPass");
        assertTrue(loginPage.getCurrentUrl().contains("/Login/GetPass"));
        assertTrue(loginPage.isForgotPasswordFormDisplayed(), "Phải có ô Mã bảo mật, Email và nút Cập nhật");
    }

    @Test
    @DisplayName("TC12 - Nhấn Enter để đăng nhập")
    void tc12_enterKeySubmits() {
        loginPage.enterUsername("someuser");
        loginPage.enterPassword("");
        loginPage.submitByEnterInUsername();
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_NO_PASSWORD, loginPage.getErrorMessage(), "Enter phải gửi form như bấm nút");

        loginPage.enterUsername("sai_user_" + System.currentTimeMillis());
        loginPage.enterPassword("sai_pass");
        loginPage.submitByEnterInPassword();
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_BAD_CREDENTIALS, loginPage.getErrorMessage());
    }

    @Test
    @DisplayName("TC13 - Username chứa khoảng trắng đầu/cuối")
    void tc13_usernameWithSpaces() {
        if (VALID_USER.isBlank()) {
            loginPage.login("  someuser  ", "Abc@12345");
            loginPage.waitUntilErrorVisible();
            assertEquals(MSG_BAD_CREDENTIALS, loginPage.getErrorMessage());
            assertTrue(loginPage.isOnLoginPage());
        } else {
            loginPage.login("  " + VALID_USER + "  ", VALID_PASS);
            // hệ thống nhất quán: hoặc chuẩn hóa (vào hệ thống) hoặc từ chối rõ ràng
            boolean loggedIn = new DashboardPage(driver).isLoaded();
            assertTrue(loggedIn || loginPage.isErrorDisplayed(), "Phải có kết quả xác định, không lỗi hệ thống");
        }
    }

    @Test
    @DisplayName("TC14 - Password chứa khoảng trắng")
    void tc14_passwordWithSpaces() {
        loginPage.login("sai_user_" + System.currentTimeMillis(), "  Abc@12345  ");
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_BAD_CREDENTIALS, loginPage.getErrorMessage());
        assertTrue(loginPage.isOnLoginPage());
    }

    @Test
    @DisplayName("TC15 - Kiểm tra SQL Injection")
    void tc15_sqlInjection() {
        loginPage.login("' OR '1'='1", "' OR '1'='1");
        loginPage.waitUntilErrorVisible();
        String page = driver.getPageSource().toLowerCase();
        assertAll(
                () -> assertEquals(MSG_BAD_CREDENTIALS, loginPage.getErrorMessage()),
                () -> assertTrue(loginPage.isOnLoginPage(), "Không được vượt qua đăng nhập"),
                () -> assertFalse(page.contains("sql syntax") || page.contains("sqlexception")
                        || page.contains("ora-0") || page.contains("stack trace"), "Không lộ lỗi SQL nội bộ"));
    }

    @Test
    @DisplayName("TC16 - Mật khẩu được che khi nhập")
    void tc16_passwordMasked() {
        loginPage.enterPassword("Abc@12345");
        assertEquals("password", loginPage.getPasswordType());
        assertEquals("Abc@12345", loginPage.getPasswordValue());
    }

    // ===================== TC17 - TC20 (bổ sung) =====================

    @Test
    @DisplayName("TC17 - Mật khẩu luôn bị che: không có nút ẩn/hiện, vẫn che sau khi submit lỗi")
    void tc17_passwordStaysMaskedNoToggle() {
        // Đã xác minh DOM: trang không có nút ẩn/hiện mật khẩu nên kiểm tra hành vi thực tế
        assertFalse(loginPage.hasPasswordToggle(), "Giao diện hiện tại không có nút toggle mật khẩu");
        loginPage.enterPassword("Abc@12345");
        loginPage.focusUsername();
        assertEquals("password", loginPage.getPasswordType(), "Mất focus không được làm lộ mật khẩu");

        loginPage.enterUsername("sai_user_" + System.currentTimeMillis());
        loginPage.clickLogin();
        loginPage.waitUntilErrorVisible();
        assertEquals("password", loginPage.getPasswordType(), "Sau submit lỗi ô mật khẩu vẫn là type=password");
        assertTrue(loginPage.getPasswordValue().isEmpty(), "Mật khẩu đã nhập không được điền lại vào form");
    }

    @Test
    @DisplayName("TC18 - Responsive trên màn hình Mobile (375x667)")
    void tc18_mobileResponsive() {
        Map<String, Object> metrics = Map.of("width", 375, "height", 667,
                "deviceScaleFactor", 2, "mobile", true);
        ((ChromeDriver) driver).executeCdpCommand("Emulation.setDeviceMetricsOverride", metrics);
        loginPage.open();
        assertAll(
                () -> assertTrue(loginPage.isLoginFormDisplayed(), "Form đăng nhập vẫn hiển thị"),
                () -> assertTrue(loginPage.isSubmitWithinViewport(), "Ô nhập và nút nằm trong chiều rộng màn hình"),
                () -> assertFalse(loginPage.hasHorizontalScroll(), "Không phát sinh thanh cuộn ngang"));
        // form vẫn dùng được trên mobile
        loginPage.clickLogin();
        loginPage.waitUntilErrorVisible();
        assertEquals(MSG_NO_USERNAME, loginPage.getErrorMessage());
    }

    @Test
    @DisplayName("TC19 - Thứ tự Tab và Focus bằng bàn phím")
    void tc19_tabOrderAndFocus() {
        loginPage.focusUsername();
        assertEquals("username", loginPage.getFocusedElementKey());
        // checkbox gốc bị ẩn nên không nhận focus
        List<String> expected = List.of("userpwd", "button", "submit_login", "/Login/GetPass");
        List<String> actual = new java.util.ArrayList<>();
        for (int i = 0; i < expected.size(); i++) {
            loginPage.pressTab();
            actual.add(loginPage.getFocusedElementKey());
        }
        assertEquals(expected, actual, "Thứ tự Tab: password -> email UTC -> Đăng nhập -> Quên mật khẩu");

        loginPage.pressShiftTab();
        assertEquals("submit_login", loginPage.getFocusedElementKey(), "Shift+Tab quay lại nút Đăng nhập");
    }

    private Set<Cookie> persistentCookies() {
        return driver.manage().getCookies().stream()
                .filter(c -> c.getExpiry() != null)
                .collect(Collectors.toSet());
    }
}
