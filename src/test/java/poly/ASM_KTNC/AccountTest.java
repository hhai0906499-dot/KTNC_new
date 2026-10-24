
package poly.ASM_KTNC;

import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class AccountTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String url = "https://automationexercise.com/";
    private final String password = "Test123456";

    private final By signupLoginLink =
            By.cssSelector("a[href='/login']");

    private final By signupName =
            By.cssSelector("[data-qa='signup-name']");

    private final By signupEmail =
            By.cssSelector("[data-qa='signup-email']");

    private final By signupButton =
            By.cssSelector("[data-qa='signup-button']");

    private final By loginEmail =
            By.cssSelector("[data-qa='login-email']");

    private final By loginPassword =
            By.cssSelector("[data-qa='login-password']");

    private final By loginButton =
            By.cssSelector("[data-qa='login-button']");

    private final By accountCreated =
            By.cssSelector("[data-qa='account-created']");

    private final By loginError =
            By.xpath(
                "//form[@action='/login']//p"
            );

    private final By signupError =
            By.xpath(
                "//form[@action='/signup']//p"
            );

    private final By loggedIn =
            By.xpath(
                "//*[contains(normalize-space(.), 'Logged in as')]"
            );

    private final By continueButton =
            By.cssSelector("[data-qa='continue-button']");

    private final By logoutLink =
            By.cssSelector("a[href='/logout']");

    @BeforeMethod
    public void beforeMethod() {
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Chi su dung explicit wait
        driver.manage().timeouts()
                .implicitlyWait(Duration.ZERO);

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        driver.get(url);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signupLoginLink
                )
        );
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // Tao email moi cho tung test can tai khoan
    private String generateEmail() {
        return "hai_" + UUID.randomUUID()
                + "@example.com";
    }

    // Mo trang dang nhap / dang ky
    private void openLoginPage() {
        if (!driver.getCurrentUrl().contains("/login")) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            signupLoginLink
                    )
            ).click();

            if (driver.getCurrentUrl().contains("#google_vignette")) {
                driver.get(url + "login");
            }
        }

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loginEmail
                )
        );
    }

    // Tao tai khoan va cho den khi hien ACCOUNT CREATED!
    private String createAccount() {
        String email = generateEmail();

        openLoginPage();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        signupName
                )
        ).sendKeys("Hai Test");

        driver.findElement(signupEmail).sendKeys(email);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signupButton
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                            "//b[contains(normalize-space(.),"
                            + "'Enter Account Information')]"
                        )
                )
        );

        driver.findElement(By.id("id_gender1")).click();

        driver.findElement(By.id("password"))
                .sendKeys(password);

        driver.findElement(By.id("days"))
                .sendKeys("10");

        driver.findElement(By.id("months"))
                .sendKeys("May");

        driver.findElement(By.id("years"))
                .sendKeys("2000");

        driver.findElement(By.id("first_name"))
                .sendKeys("Hoang");

        driver.findElement(By.id("last_name"))
                .sendKeys("Hai");

        driver.findElement(By.id("address1"))
                .sendKeys("123 Test Street");

        driver.findElement(By.id("country"))
                .sendKeys("India");

        driver.findElement(By.id("state"))
                .sendKeys("Test State");

        driver.findElement(By.id("city"))
                .sendKeys("Test City");

        driver.findElement(By.id("zipcode"))
                .sendKeys("12345");

        driver.findElement(By.id("mobile_number"))
                .sendKeys("0123456789");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                            "[data-qa='create-account']"
                        )
                )
        ).click();

        WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        accountCreated
                )
        );

        Assert.assertEquals(
                result.getText().trim(),
                "ACCOUNT CREATED!",
                "Tao tai khoan that bai!"
        );

        return email;
    }

    // Tiep tuc sau khi tao tai khoan va dang xuat
    private void logout() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        continueButton
                )
        ).click();

        if (driver.getCurrentUrl().contains("#google_vignette")) {
            driver.get(url);
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        logoutLink
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loginEmail
                )
        );
    }

    // TC01: Dang ky tai khoan moi
    @Test
    public void register_Success() {
        createAccount();

        WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        accountCreated
                )
        );

        Assert.assertEquals(
                result.getText().trim(),
                "ACCOUNT CREATED!"
        );
    }

    // TC02: Dang ky bang email da ton tai
    @Test
    public void register_Fail() {
        // Tao tai khoan that truoc de dam bao email ton tai
        String email = createAccount();

        // Dang xuat tai khoan vua tao
        logout();

        // Thu dang ky lai bang email cu
        openLoginPage();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        signupName
                )
        );
        nameInput.clear();
        nameInput.sendKeys("Hai Test");

        WebElement emailInput = driver.findElement(signupEmail);
        emailInput.clear();
        emailInput.sendKeys(email);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signupButton
                )
        ).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        signupError
                )
        );

        Assert.assertTrue(
                error.getText().toLowerCase()
                        .contains("email address already exist"),
                "Khong hien thi loi email da ton tai!"
        );
    }

    // TC03: Dang nhap thanh cong
    @Test
    public void login_Success() {
        // Tao tai khoan rieng cho test nay
        String email = createAccount();

        // Dang xuat de kiem tra chuc nang dang nhap
        logout();

        openLoginPage();

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loginEmail
                )
        );
        emailInput.clear();
        emailInput.sendKeys(email);

        WebElement passInput = driver.findElement(loginPassword);
        passInput.clear();
        passInput.sendKeys(password);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        loginButton
                )
        ).click();

        // Cho thong bao thanh cong hoac that bai
        wait.until(d ->
                !d.findElements(loggedIn).isEmpty()
                || !d.findElements(loginError).isEmpty()
        );

        if (!driver.findElements(loggedIn).isEmpty()) {
            Assert.assertTrue(
                    driver.findElement(loggedIn).isDisplayed(),
                    "Khong hien thi Logged in as!"
            );
        } else {
            Assert.fail(
                    "Dang nhap that bai: "
                    + driver.findElement(loginError).getText()
            );
        }

        Assert.assertTrue(
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                        logoutLink
                    )
                ).isDisplayed(),
                "Khong tim thay Logout!"
        );
    }

    // TC04: Dang nhap sai mat khau
    @Test
    public void login_Fail() {
        openLoginPage();

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loginEmail
                )
        );
        emailInput.clear();
        emailInput.sendKeys("invalid_" + UUID.randomUUID()
                + "@example.com");

        WebElement passInput = driver.findElement(loginPassword);
        passInput.clear();
        passInput.sendKeys("WrongPassword123");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        loginButton
                )
        ).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loginError
                )
        );

        Assert.assertTrue(
                error.getText().toLowerCase()
                        .contains("incorrect"),
                "Khong hien thi thong bao dang nhap sai!"
        );
    }

    // TC05: Bo trong email va password
    @Test
    public void login_Empty() {
        openLoginPage();

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loginEmail
                )
        );

        WebElement pass = driver.findElement(loginPassword);

        Assert.assertEquals(email.getAttribute("value"), "");
        Assert.assertEquals(pass.getAttribute("value"), "");

        driver.findElement(loginButton).click();

        // Form van phai o trang login
        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Trang da roi khoi trang dang nhap!"
        );

        Assert.assertEquals(
                email.getAttribute("value"),
                "",
                "Email khong con trong!"
        );

        Assert.assertEquals(
                pass.getAttribute("value"),
                "",
                "Password khong con trong!"
        );
    }
}
