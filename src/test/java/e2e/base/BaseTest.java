package e2e.base;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public abstract class BaseTest {

    protected static final String BASE_URL =
            System.getProperty("baseUrl", "https://vanphongdientu.utc.edu.vn");
    protected static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("headless", "true"));

    protected WebDriver driver;

    @BeforeEach
    void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        if (HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,768", "--disable-gpu",
                "--no-sandbox", "--disable-dev-shm-usage", "--lang=vi");
        options.setAcceptInsecureCerts(true);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
        if (!HEADLESS) {
            driver.manage().window().maximize();
        }
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
                // trình duyệt có thể đã đóng
            } finally {
                driver = null;
            }
        }
    }
}
