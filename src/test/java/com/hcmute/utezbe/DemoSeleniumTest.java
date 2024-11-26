package com.hcmute.utezbe;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.json.JSONObject;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DemoSeleniumTest {
    private static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
    }

    @BeforeEach
    public void setUpEach() {

    }


    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginToWebpage(String email, String password) throws IOException {
        String loginUrl = "http://localhost:9191/api/v1/auth/authenticate";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(loginUrl);

        String json = new JSONObject()
                .put("email", email)
                .put("password", password)
                .toString();

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity());
        client.close();

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK || responseBody.isEmpty()) {
            throw new IOException("Failed to get a valid response from the server");
        }

        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONObject userData = jsonResponse.getJSONObject("data");
        driver.get("http://localhost:3000");
        Cookie cookie = new Cookie.Builder("access_token", userData.getString("accessToken"))
                .build();
        driver.manage().addCookie(cookie);

        ((JavascriptExecutor) driver).executeScript(
                "const user = {" +
                        "'fullName': arguments[0], 'email': arguments[1], 'avatar': arguments[2], 'role': arguments[3]" +
                        "};" +
                        "localStorage.setItem('user', JSON.stringify(user));",
                userData.getString("fullName"),
                userData.getString("email"),
                userData.getString("avatar"),
                userData.getString("role")
        );
        driver.navigate().refresh();
    }

    @Test
    @Order(1)
    public void testLoginSuccessful() throws InterruptedException {
        driver.get("http://localhost:3000/login");
        driver.findElement(By.id("email")).sendKeys("21110606@student.hcmute.edu.vn");
        driver.findElement(By.id("password")).sendKeys("@123456");
        driver.findElement(By.xpath("/html/body/div/div/div/div/div[2]/div/form/input")).click();
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/nav/div")));
        Thread.sleep(2000);
        Assertions.assertEquals("http://localhost:3000/my-course", driver.getCurrentUrl());
    }

    @Test
    @Order(2)
    public void testOpenProfilePage() throws InterruptedException, IOException {
        loginToWebpage("21110606@student.hcmute.edu.vn", "@123456");
        driver.get("http://localhost:3000");
        Wait<WebDriver> wait =
                new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(2))
                        .pollingEvery(Duration.ofMillis(300))
                        .ignoring(ElementNotInteractableException.class);
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("/html/body/div/div/div/div[1]/nav/div/div/div[2]/div/div/button"))));

        driver.findElement(By.xpath("/html/body/div/div/div/div[1]/nav/div/div/div[2]/div/div/button")).click();
        driver.findElement(By.xpath("/html/body/div/div/div/div[1]/nav/div/div/div[2]/div/div/div/a[1]")).click();
        Thread.sleep(2000);
        Assertions.assertEquals("http://localhost:3000/profile", driver.getCurrentUrl());
    }

}