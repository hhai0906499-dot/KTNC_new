package poly.ASM_KTNC;

import java.time.Duration;

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

public class ProductTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String url = "https://automationexercise.com/";

    private final By productsLink =
            By.cssSelector("a[href='/products']");

    private final By searchInput =
            By.id("search_product");

    private final By searchButton =
            By.id("submit_search");

    private final By allProductsTitle =
            By.xpath("//h2[contains(@class,'title') and contains(translate(., 'all products', 'ALL PRODUCTS'), 'ALL PRODUCTS')]");

    private final By searchedProductsTitle =
            By.xpath("//h2[contains(@class,'title') and contains(translate(., 'searched products', 'SEARCHED PRODUCTS'), 'SEARCHED PRODUCTS')]");

    private final By productWrapper =
            By.cssSelector(".product-image-wrapper");

    private final By firstProductDetailLink =
            By.cssSelector("a[href='/product_details/1']");

    private final By productInformation =
            By.cssSelector(".product-information");

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
                        productsLink
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

    // Mo trang Products
    private void openProducts() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        productsLink
                )
        ).click();

        if (driver.getCurrentUrl().contains("#google_vignette")) {
            driver.get(url + "products");
        }

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        allProductsTitle
                )
        );
    }

    // TC06: Hien thi danh sach san pham
    @Test
    public void displayProducts_Success() {
        openProducts();

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        allProductsTitle
                )
        );

        Assert.assertTrue(
                title.getText().toLowerCase().contains("all products"),
                "Tieu de khong chua All Products!"
        );

        Assert.assertTrue(
                driver.findElements(productWrapper).size() > 0,
                "Khong co san pham nao duoc hien thi!"
        );
    }

    // TC07: Tim kiem san pham ton tai
    @Test
    public void searchProduct_Success() {
        openProducts();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        searchInput
                )
        ).sendKeys("Blue Top");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        searchButton
                )
        ).click();

        if (driver.getCurrentUrl().contains("#google_vignette")) {
            driver.get(url + "products?search=Blue%20Top");
        }

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        searchedProductsTitle
                )
        );

        Assert.assertTrue(
                title.isDisplayed(),
                "Khong hien thi tieu de SEARCHED PRODUCTS!"
        );

        Assert.assertTrue(
                driver.getPageSource().contains("Blue Top"),
                "Khong tim thay san pham Blue Top!"
        );
    }

    // TC08: Tim kiem san pham khong ton tai
    @Test
    public void searchProduct_NotFound() {
        openProducts();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        searchInput
                )
        ).sendKeys("ABCXYZ123_NOT_FOUND");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        searchButton
                )
        ).click();

        if (driver.getCurrentUrl().contains("#google_vignette")) {
            driver.get(url + "products?search=ABCXYZ123_NOT_FOUND");
        }

        wait.until(ExpectedConditions.urlContains("search"));

        // Cho danh sach ket qua cap nhat
        wait.until(d ->
                d.findElements(productWrapper).isEmpty()
                || d.findElements(productWrapper).stream().noneMatch(WebElement::isDisplayed)
        );

        Assert.assertTrue(
                driver.findElements(productWrapper).stream().noneMatch(WebElement::isDisplayed),
                "Khong mong doi co san pham phu hop!"
        );
    }

    // TC09: Xem chi tiet san pham
    @Test
    public void viewProduct_Success() {
        openProducts();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        firstProductDetailLink
                )
        ).click();

        if (driver.getCurrentUrl().contains("#google_vignette")) {
            driver.get(url + "product_details/1");
        }

        WebElement detail = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        productInformation
                )
        );

        Assert.assertTrue(detail.isDisplayed(), "Thong tin san pham khong hien thi!");
        Assert.assertTrue(detail.getText().contains("Blue Top"), "Khong dung ten san pham!");
        Assert.assertTrue(detail.getText().contains("Rs."), "Khong tim thay gia tien!");
    }

    // TC10: Tim kiem theo mot phan ten san pham
    @Test
    public void searchProduct_PartialName() {
        openProducts();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        searchInput
                )
        ).sendKeys("Top");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        searchButton
                )
        ).click();

        if (driver.getCurrentUrl().contains("#google_vignette")) {
            driver.get(url + "products?search=Top");
        }

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        searchedProductsTitle
                )
        );

        Assert.assertTrue(
                title.isDisplayed(),
                "Khong hien thi tieu de SEARCHED PRODUCTS!"
        );

        Assert.assertTrue(
                driver.findElements(productWrapper).stream().anyMatch(WebElement::isDisplayed),
                "Khong tim thay bat ky san pham nao phu hop!"
        );

        Assert.assertTrue(
                driver.getPageSource().contains("Top"),
                "Noi dung trang khong chua tu Top!"
        );
    }
}