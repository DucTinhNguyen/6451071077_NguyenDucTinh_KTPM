package e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Trang sau đăng nhập. DOM của khu vực sau đăng nhập chưa được xác minh (cần tài khoản thật),
 * nên chỉ kiểm tra các dấu hiệu chắc chắn: đã rời /Login và không còn form đăng nhập.
 */
public class DashboardPage extends BasePage {

    private static final By LOGIN_PASSWORD = By.name("userpwd");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            wait.until(d -> !d.getCurrentUrl().contains("/Login/") && !d.getCurrentUrl().endsWith("/Login")
                    && d.findElements(LOGIN_PASSWORD).isEmpty());
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
