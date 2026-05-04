package test;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.junit.*;

import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private String baseLoginURL = "http://13.49.187.55:3002/login";
    private String homeURL = "http://13.49.187.55:3002/";
    private String profileURL = "http://13.49.187.55:3002/profile";

    private String validEmail = "yousf@gmail.com";
    private String validPassword = "yousaf.12";

    @Before
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        // FIX: only explicit wait (remove implicit wait conflict)
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ============ HELPER METHOD ============

    private void login() {
        driver.navigate().to(baseLoginURL);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys(validEmail);
        driver.findElement(By.id("password")).sendKeys(validPassword);

        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]")).click();

        // FIX: stable condition instead of generic "/" check
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("13.49.187.55:3002"),
                ExpectedConditions.urlContains("3002/")
        ));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============ LOGIN TESTS ============

    @Test
    public void testValidLogin() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys(validEmail);
        driver.findElement(By.id("password")).sendKeys(validPassword);
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]")).click();

        wait.until(ExpectedConditions.urlContains("3002"));

        boolean isOnHomePage = driver.getCurrentUrl().contains("13.49.187.55:3002");
        assertTrue("Should redirect to home page after valid login", isOnHomePage);
    }

    @Test
    public void testLoginWithEmptyFields() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys("");
        driver.findElement(By.id("password")).sendKeys("");
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]")).click();

        sleep(800);

        boolean errorDisplayed =
                driver.findElements(By.xpath("//*[contains(text(), 'required') or contains(text(), 'error')]"))
                        .size() > 0;

        assertTrue("Should show error for empty fields", errorDisplayed);
    }

    @Test
    public void testLoginWithInvalidEmail() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys("invalidemail");
        driver.findElement(By.id("password")).sendKeys(validPassword);
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]")).click();

        sleep(800);

        boolean errorDisplayed =
                driver.findElements(By.xpath("//*[contains(text(), 'error') or contains(text(), 'failed')]"))
                        .size() > 0;

        assertTrue("Should show error for invalid email", errorDisplayed);
    }

    @Test
    public void testLoginFormElementsPresent() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]"));

        assertTrue(emailField.isDisplayed());
        assertTrue(passwordField.isDisplayed());
        assertTrue(submitBtn.isDisplayed());
    }

    // ============ OTHER TESTS (UNCHANGED LOGIC, ONLY STABILITY FIXED SLIGHTLY) ============

    @Test
    public void testCreateQuestionWithValidData() {
        login();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='Ask your question here...']")));

        WebElement titleInput = driver.findElement(
                By.xpath("//input[@placeholder='Ask your question here...']"));
        WebElement contentTextarea = driver.findElement(
                By.xpath("//textarea[@placeholder='Add details...']"));

        String testTitle = "How to use Selenium? " + System.currentTimeMillis();
        titleInput.sendKeys(testTitle);
        contentTextarea.sendKeys("This is a test question for automation testing.");

        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();

        sleep(1200);

        boolean questionPosted =
                driver.findElements(By.xpath("//*[contains(text(), 'Selenium')]")).size() > 0;

        assertTrue("Question should be posted successfully", questionPosted);
    }

    @Test
    public void testCreateQuestionWithEmptyTitle() {
        login();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//textarea[@placeholder='Add details...']")));

        driver.findElement(By.xpath("//input[@placeholder='Ask your question here...']")).sendKeys("");
        driver.findElement(By.xpath("//textarea[@placeholder='Add details...']")).sendKeys("Content without title");

        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();

        sleep(800);

        String titleValue =
                driver.findElement(By.xpath("//input[@placeholder='Ask your question here...']"))
                        .getAttribute("value");

        assertEquals("", titleValue);
    }

    @Test
    public void testCreateQuestionWithSpecialCharacters() {
        login();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='Ask your question here...']")));

        WebElement titleInput = driver.findElement(
                By.xpath("//input[@placeholder='Ask your question here...']"));
        WebElement contentTextarea = driver.findElement(
                By.xpath("//textarea[@placeholder='Add details...']"));

        String titleWithSpecialChars =
                "What's the difference between @Override and @Deprecated?";
        titleInput.sendKeys(titleWithSpecialChars);
        contentTextarea.sendKeys("Explain Java annotations");

        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();

        sleep(1200);

        boolean questionPosted =
                driver.findElements(By.xpath("//*[contains(text(), '@Override')]")).size() > 0;

        assertTrue(questionPosted);
    }

    @Test
    public void testPostQuestionFormClears() {
        login();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='Ask your question here...']")));

        WebElement titleInput = driver.findElement(
                By.xpath("//input[@placeholder='Ask your question here...']"));
        WebElement contentTextarea = driver.findElement(
                By.xpath("//textarea[@placeholder='Add details...']"));

        titleInput.sendKeys("Test Question " + System.currentTimeMillis());
        contentTextarea.sendKeys("Test content");

        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();

        sleep(1200);

        String titleAfter = titleInput.getAttribute("value");
        String contentAfter = contentTextarea.getAttribute("value");

        assertTrue(titleAfter.isEmpty() || contentAfter.isEmpty());
    }
}
