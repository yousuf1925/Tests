package test;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.junit.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--remote-allow-origins=*");

    driver = new ChromeDriver(options);

    // ✅ Selenium 3 compatible
    driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

    // ❌ REMOVE implicit wait (avoid conflicts)
    // driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    // ✅ Selenium 3 style
    wait = new WebDriverWait(driver, 20);
}

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ===== HELPER METHOD =====

    private void login() {
        driver.navigate().to(baseLoginURL);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys(validEmail);
        driver.findElement(By.id("password")).sendKeys(validPassword);

        driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();

        wait.until(ExpectedConditions.urlContains("3002"));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ===== ALL TEST CASES BELOW (UNCHANGED LOGIC) =====

    @Test
    public void testValidLogin() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys(validEmail);
        driver.findElement(By.id("password")).sendKeys(validPassword);
        driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();

        wait.until(ExpectedConditions.urlContains("3002"));

        assertTrue(driver.getCurrentUrl().contains("3002"));
    }

    @Test
    public void testLoginWithEmptyFields() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys("");
        driver.findElement(By.id("password")).sendKeys("");
        driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();

        sleep(800);

        boolean errorDisplayed = driver.findElements(
                By.xpath("//*[contains(text(),'required') or contains(text(),'error')]")
        ).size() > 0;

        assertTrue(errorDisplayed);
    }

    @Test
    public void testLoginWithInvalidEmail() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys("invalidemail");
        driver.findElement(By.id("password")).sendKeys(validPassword);
        driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();

        sleep(800);

        boolean errorDisplayed = driver.findElements(
                By.xpath("//*[contains(text(),'error') or contains(text(),'failed')]")
        ).size() > 0;

        assertTrue(errorDisplayed);
    }

    @Test
    public void testLoginFormElementsPresent() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        assertTrue(driver.findElement(By.id("email")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElement(By.xpath("//button[contains(text(),'Login')]")).isDisplayed());
    }

    // ===== QUESTION TESTS =====

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

        assertTrue(driver.findElements(By.xpath("//*[contains(text(), 'Selenium')]")).size() > 0);
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

        String title = "What's the difference between @Override and @Deprecated?";
        titleInput.sendKeys(title);
        contentTextarea.sendKeys("Explain Java annotations");

        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();

        sleep(1200);

        assertTrue(driver.findElements(By.xpath("//*[contains(text(), '@Override')]")).size() > 0);
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

        titleInput.sendKeys("Test " + System.currentTimeMillis());
        contentTextarea.sendKeys("Test content");

        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();

        sleep(1200);

        assertTrue(titleInput.getAttribute("value").isEmpty() ||
                   contentTextarea.getAttribute("value").isEmpty());
    }

    // ===== NAVIGATION & ANSWERS =====

    @Test
    public void testNavigateToQuestionDetail() {
        login();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/questions/')]"))).click();

        sleep(1200);

        assertTrue(driver.getCurrentUrl().contains("/questions/"));
    }

    @Test
    public void testPostAnswerToQuestion() {
        login();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/questions/')]"))).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//textarea")));

        String answer = "test answer " + System.currentTimeMillis();

        driver.findElement(By.xpath("//textarea")).sendKeys(answer);
        driver.findElement(By.xpath("//button[contains(text(),'Post Answer')]")).click();

        sleep(1200);

        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'test answer')]")).size() > 0);
    }

    @Test
    public void testAnswersDisplayedOnQuestionPage() {
        login();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/questions/')]"))).click();

        sleep(1000);

        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'Answers')]")).size() > 0);
    }

    // ===== PROFILE & LOGOUT =====

    @Test
    public void testNavigateToProfilePage() {
        login();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/profile')]"))).click();

        assertTrue(driver.getCurrentUrl().contains("/profile"));
    }

    @Test
    public void testLogout() {
        login();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Logout')]"))).click();

        assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}
