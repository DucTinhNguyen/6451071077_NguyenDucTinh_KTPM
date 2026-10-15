package e2e.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected static final Duration TIMEOUT = Duration.ofSeconds(15);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected String getAttribute(By locator, String attribute) {
        return waitPresent(locator).getAttribute(attribute);
    }

    protected boolean isDisplayed(By locator) {
        return !driver.findElements(locator).isEmpty()
                && driver.findElements(locator).get(0).isDisplayed();
    }

    protected boolean exists(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    public void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }
}
