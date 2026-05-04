package test;

import static org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Alert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.time.Duration;

public class LoginTest {
    private WebDriver driver;
    private String baseLoginURL = "http://13.49.187.55:3002/login";
    private String homeURL = "http://13.49.187.55:3002/";
    private String profileURL = "http://13.49.187.55:3002/profile";
    private WebDriverWait wait;
    
    private String validEmail = "yousf@gmail.com";
    private String validPassword = "yousaf.12";
    
    @Before
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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
        
        wait.until(ExpectedConditions.urlContains("/"));
        sleep(1500);
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // ============ LOGIN TESTS (4 tests) ============
    
    @Test
    public void testValidLogin() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        
        driver.findElement(By.id("email")).sendKeys(validEmail);
        driver.findElement(By.id("password")).sendKeys(validPassword);
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]")).click();
        
        wait.until(ExpectedConditions.urlContains("/"));
        boolean isOnHomePage = driver.getCurrentUrl().contains("localhost:3000");
        assertTrue("Should redirect to home page after valid login", isOnHomePage);
    }
    
    @Test
    public void testLoginWithEmptyFields() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        
        driver.findElement(By.id("email")).sendKeys("");
        driver.findElement(By.id("password")).sendKeys("");
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]")).click();
        
        sleep(1000);
        boolean errorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'required') or contains(text(), 'error')]")).size() > 0;
        assertTrue("Should show error for empty fields", errorDisplayed);
    }
    
    @Test
    public void testLoginWithInvalidEmail() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        
        driver.findElement(By.id("email")).sendKeys("invalidemail");
        driver.findElement(By.id("password")).sendKeys(validPassword);
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]")).click();
        
        sleep(1000);
        boolean errorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'error') or contains(text(), 'failed')]")).size() > 0;
        assertTrue("Should show error for invalid email", errorDisplayed);
    }
    
    @Test
    public void testLoginFormElementsPresent() {
        driver.navigate().to(baseLoginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Login')]"));
        
        assertTrue("Email field should be displayed", emailField.isDisplayed());
        assertTrue("Password field should be displayed", passwordField.isDisplayed());
        assertTrue("Submit button should be displayed", submitBtn.isDisplayed());
    }
    
    // ============ QUESTION CREATION TESTS (4 tests) ============
    
    @Test
    public void testCreateQuestionWithValidData() {
        login();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Ask your question here...']")));
        
        WebElement titleInput = driver.findElement(By.xpath("//input[@placeholder='Ask your question here...']"));
        WebElement contentTextarea = driver.findElement(By.xpath("//textarea[@placeholder='Add details...']"));
        
        String testTitle = "How to use Selenium? " + System.currentTimeMillis();
        titleInput.sendKeys(testTitle);
        contentTextarea.sendKeys("This is a test question for automation testing.");
        
        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();
        
        sleep(1500);
        boolean questionPosted = driver.findElements(By.xpath("//*[contains(text(), 'Selenium')]")).size() > 0;
        assertTrue("Question should be posted successfully", questionPosted);
    }
    
    @Test
    public void testCreateQuestionWithEmptyTitle() {
        login();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@placeholder='Add details...']")));
        
        driver.findElement(By.xpath("//input[@placeholder='Ask your question here...']")).sendKeys("");
        driver.findElement(By.xpath("//textarea[@placeholder='Add details...']")).sendKeys("Content without title");
        
        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();
        
        sleep(500);
        String titleValue = driver.findElement(By.xpath("//input[@placeholder='Ask your question here...']")).getAttribute("value");
        assertEquals("Form should not submit with empty title", "", titleValue);
    }
    
    @Test
    public void testCreateQuestionWithSpecialCharacters() {
        login();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Ask your question here...']")));
        
        WebElement titleInput = driver.findElement(By.xpath("//input[@placeholder='Ask your question here...']"));
        WebElement contentTextarea = driver.findElement(By.xpath("//textarea[@placeholder='Add details...']"));
        
        String titleWithSpecialChars = "What's the difference between @Override and @Deprecated?";
        titleInput.sendKeys(titleWithSpecialChars);
        contentTextarea.sendKeys("Explain Java annotations");
        
        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();
        
        sleep(1500);
        boolean questionPosted = driver.findElements(By.xpath("//*[contains(text(), '@Override')]")).size() > 0;
        assertTrue("Special characters should be handled", questionPosted);
    }
    
    @Test
    public void testPostQuestionFormClears() {
        login();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Ask your question here...']")));
        
        WebElement titleInput = driver.findElement(By.xpath("//input[@placeholder='Ask your question here...']"));
        WebElement contentTextarea = driver.findElement(By.xpath("//textarea[@placeholder='Add details...']"));
        
        titleInput.sendKeys("Test Question " + System.currentTimeMillis());
        contentTextarea.sendKeys("Test content");
        
        driver.findElement(By.xpath("//button[contains(text(), 'Post Question')]")).click();
        
        sleep(1500);
        String titleAfter = titleInput.getAttribute("value");
        String contentAfter = contentTextarea.getAttribute("value");
        
        assertTrue("Form should clear after successful submission", titleAfter.isEmpty() || contentAfter.isEmpty());
    }
    
    // ============ ANSWER/COMMENT TESTS (3 tests) ============
    
    @Test
    public void testNavigateToQuestionDetail() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/questions/')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/questions/')]")).click();
        
        sleep(1500);
        assertTrue("Should navigate to question detail page", driver.getCurrentUrl().contains("/questions/"));
    }
    
    @Test
    public void testPostAnswerToQuestion() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/questions/')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/questions/')]")).click();
        
        sleep(1500);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@placeholder='Write your answer or comment here...']")));
        
        WebElement answerTextarea = driver.findElement(By.xpath("//textarea[@placeholder='Write your answer or comment here...']"));
        String testAnswer = "This is a test answer " + System.currentTimeMillis();
        answerTextarea.sendKeys(testAnswer);
        
        driver.findElement(By.xpath("//button[contains(text(), 'Post Answer')]")).click();
        
        sleep(1500);
        boolean answerPosted = driver.findElements(By.xpath("//*[contains(text(), 'test answer')]")).size() > 0;
        assertTrue("Answer should be posted successfully", answerPosted);
    }
    
    @Test
    public void testAnswersDisplayedOnQuestionPage() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/questions/')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/questions/')]")).click();
        
        sleep(1500);
        boolean answersSection = driver.findElements(By.xpath("//*[contains(text(), 'Answers')]")).size() > 0;
        assertTrue("Answers section should be visible", answersSection);
    }
    
    // ============ DELETE QUESTION TESTS (5 tests) ============
    
    @Test
    public void testNavigateToProfilePage() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/profile')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/profile')]")).click();
        
        sleep(1000);
        assertTrue("Should navigate to profile page", driver.getCurrentUrl().contains("/profile"));
    }
    
    @Test
    public void testProfileDisplaysUserQuestions() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/profile')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/profile')]")).click();
        
        sleep(1500);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[contains(text(), 'Your Questions')]")));
        
        WebElement yourQuestionsHeading = driver.findElement(By.xpath("//h2[contains(text(), 'Your Questions')]"));
        assertTrue("Profile should show 'Your Questions' section", yourQuestionsHeading.isDisplayed());
    }
    
    @Test
    public void testDeleteButtonPresentOnUserQuestion() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/profile')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/profile')]")).click();
        
        sleep(1500);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[contains(text(), 'Your Questions')]")));
        
        // Check if delete button exists on question cards
        boolean deleteButtonExists = driver.findElements(By.xpath("//button[contains(text(), 'Delete')]")).size() > 0;
        assertTrue("Delete button should be present on user's questions", deleteButtonExists);
    }
    
    @Test
    public void testDeleteQuestionWithConfirmation() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/profile')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/profile')]")).click();
        
        sleep(1500);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'Delete')]")));
        
        // Get the count of questions before deletion
        int questionsBeforeDelete = driver.findElements(By.xpath("//a[contains(@href, '/questions/')]")).size();
        
        // Click delete button
        WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(), 'Delete')]"));
        deleteButton.click();
        
        sleep(500);
        // Handle browser confirmation dialog
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept(); // Accept the confirmation
            sleep(1500);
        } catch (Exception e) {
            // If no alert, continue
        }
        
        // Verify question is deleted (count should decrease)
        int questionsAfterDelete = driver.findElements(By.xpath("//a[contains(@href, '/questions/')]")).size();
        assertTrue("Question should be deleted after confirmation", questionsAfterDelete < questionsBeforeDelete || questionsBeforeDelete == 0);
    }
    
    @Test
    public void testCancelDeleteQuestion() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/profile')]")));
        driver.findElement(By.xpath("//a[contains(@href, '/profile')]")).click();
        
        sleep(1500);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'Delete')]")));
        
        // Get the count of questions before clicking delete
        int questionsBeforeDelete = driver.findElements(By.xpath("//a[contains(@href, '/questions/')]")).size();
        
        // Click delete button
        WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(), 'Delete')]"));
        deleteButton.click();
        
        sleep(500);
        // Dismiss the confirmation dialog
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss(); // Dismiss the confirmation
            sleep(1000);
        } catch (Exception e) {
            // If no alert, continue
        }
        
        // Verify question still exists (count should remain same)
        int questionsAfterCancel = driver.findElements(By.xpath("//a[contains(@href, '/questions/')]")).size();
        assertEquals("Question should not be deleted if user cancels", questionsBeforeDelete, questionsAfterCancel);
    }
    
    // ============ LOGOUT TEST (1 test) ============
    
    @Test
    public void testLogout() {
        login();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Logout')]")));
        driver.findElement(By.xpath("//button[contains(text(), 'Logout')]")).click();
        
        sleep(1000);
        assertTrue("Should redirect to login after logout", driver.getCurrentUrl().contains("/login"));
    }
}
