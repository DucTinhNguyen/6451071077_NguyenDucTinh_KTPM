package e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object cho https://vanphongdientu.utc.edu.vn/Login (locator đã xác minh từ DOM thực tế). */
public class LoginPage extends BasePage {

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("userpwd");
    private static final By SUBMIT = By.cssSelector("input.submit_login");
    private static final By ERROR = By.cssSelector("form div.error");
    // checkbox gốc bị JS ẩn đi, thay bằng label.check hiển thị
    private static final By PERSISTENT_INPUT = By.id("persistent");
    private static final By PERSISTENT_LABEL = By.cssSelector("label.check[for='persistent']");
    private static final By EMAIL_UTC_LINK = By.cssSelector("form a.button");
    private static final By FORGOT_LINK = By.cssSelector("div.helps a[href='/Login/GetPass']");
    private static final By BANNER_IMG = By.cssSelector("div.left img");
    private static final By BANNER_CAPTION = By.cssSelector("div.left div.caption h1");
    private static final By FOOTER_HELP = By.cssSelector("div.footer a[href*='hotrokythuat.utc.edu.vn']");
    private static final By FOOTER_FEEDBACK = By.cssSelector("div.footer a[href^='mailto:']");
    private static final By PASSWORD_TOGGLE = By.cssSelector(
            "[class*='toggle'], [class*='eye'], [class*='show-pass'], [id*='toggle'], [id*='showpass']");

    private final String baseUrl;

    public LoginPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public LoginPage open() {
        driver.get(baseUrl + "/Login");
        waitVisible(USERNAME);
        waitVisible(PASSWORD);
        return this;
    }

    public LoginPage enterUsername(String username) {
        type(USERNAME, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public void clickLogin() {
        click(SUBMIT);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public void submitByEnterInPassword() {
        waitVisible(PASSWORD).sendKeys(Keys.ENTER);
    }

    public void submitByEnterInUsername() {
        waitVisible(USERNAME).sendKeys(Keys.ENTER);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR);
    }

    public String getErrorMessage() {
        return getText(ERROR);
    }

    public boolean isOnLoginPage() {
        return getCurrentUrl().contains("/Login") && isDisplayed(PASSWORD);
    }

    public String getUsernameValue() {
        return getAttribute(USERNAME, "value");
    }

    public String getPasswordValue() {
        return getAttribute(PASSWORD, "value");
    }

    public String getPasswordType() {
        return getAttribute(PASSWORD, "type");
    }

    public String getPasswordPlaceholder() {
        return getAttribute(PASSWORD, "placeholder");
    }

    public String getUsernamePlaceholder() {
        return getAttribute(USERNAME, "placeholder");
    }

    public boolean hasPasswordToggle() {
        return exists(PASSWORD_TOGGLE);
    }

    public void clickPersistentCheckbox() {
        click(PERSISTENT_LABEL);
    }

    public boolean isPersistentChecked() {
        return waitPresent(PERSISTENT_INPUT).isSelected();
    }

    public String clickEmailUtcLink() {
        String href = getAttribute(EMAIL_UTC_LINK, "href");
        click(EMAIL_UTC_LINK);
        return href;
    }

    public String getEmailUtcHref() {
        return getAttribute(EMAIL_UTC_LINK, "href");
    }

    public void clickForgotPassword() {
        click(FORGOT_LINK);
    }

    public boolean isLoginFormDisplayed() {
        return isDisplayed(USERNAME) && isDisplayed(PASSWORD) && isDisplayed(SUBMIT);
    }

    public boolean isSubmitDisplayed() {
        return isDisplayed(SUBMIT);
    }

    public boolean isBannerDisplayed() {
        return isDisplayed(BANNER_IMG) && isDisplayed(BANNER_CAPTION);
    }

    public void clickBanner() {
        click(BANNER_IMG);
    }

    /** Số thẻ a bao quanh ảnh banner (0 nghĩa là banner không phải liên kết). */
    public long countLinksWrappingBanner() {
        return (Long) executeScript(
                "return document.querySelectorAll('div.left a').length "
                        + "+ (arguments[0].closest('a') ? 1 : 0);",
                waitPresent(BANNER_IMG));
    }

    public String getFooterHelpHref() {
        return getAttribute(FOOTER_HELP, "href");
    }

    public String getFooterFeedbackHref() {
        return getAttribute(FOOTER_FEEDBACK, "href");
    }

    public boolean hasHorizontalScroll() {
        return (Boolean) executeScript(
                "return document.documentElement.scrollWidth > document.documentElement.clientWidth;");
    }

    /** Phần tử có nằm hoàn toàn trong chiều rộng viewport hay không. */
    public boolean isSubmitWithinViewport() {
        return isWithinViewportWidth(waitVisible(SUBMIT))
                && isWithinViewportWidth(waitVisible(USERNAME))
                && isWithinViewportWidth(waitVisible(PASSWORD));
    }

    private boolean isWithinViewportWidth(WebElement element) {
        return (Boolean) executeScript(
                "var r = arguments[0].getBoundingClientRect();"
                        + "return r.left >= 0 && r.right <= document.documentElement.clientWidth;",
                element);
    }

    public void focusUsername() {
        click(USERNAME);
    }

    public void pressTab() {
        driver.switchTo().activeElement().sendKeys(Keys.TAB);
    }

    public void pressShiftTab() {
        driver.switchTo().activeElement().sendKeys(Keys.SHIFT, Keys.TAB);
    }

    /** Định danh phần tử đang focus: name, hoặc class, hoặc href. */
    public String getFocusedElementKey() {
        return (String) executeScript(
                "var e = document.activeElement;"
                        + "return e.name || e.getAttribute('class') || e.getAttribute('href') || e.tagName;");
    }

    public boolean isForgotPasswordFormDisplayed() {
        By email = By.name("email");
        By captcha = By.name("captcha");
        By update = By.cssSelector("form[action='/Login/Getpass'] input[type='submit']");
        waitVisible(email);
        return isDisplayed(email) && isDisplayed(captcha) && isDisplayed(update);
    }

    public void waitUntilErrorVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR));
    }
}
