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

public class CartTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String url = "https://automationexercise.com/";

    private final By viewCartLink =
            By.cssSelector("a[href='/view_cart']");

    private final By quantityInput =
            By.id("quantity");

    private final By addToCartButton =
            By.cssSelector("button.cart");

    private final By cartModal =
            By.id("cartModal");

    private final By modalViewCartLink =
            By.cssSelector("#cartModal a[href='/view_cart']");

    private final By product1Row =
            By.id("product-1");

    private final By product2Row =
            By.id("product-2");

    private final By cartRows =
            By.cssSelector("#cart_info_table tbody tr[id^='product-']");

    private final By product1Quantity =
            By.cssSelector("#product-1 .cart_quantity button");

    private final By product1DeleteButton =
            By.cssSelector("#product-1 .cart_quantity_delete");

    private final By cartPrice =
            By.cssSelector(".cart_price");

    private final By cartTotal =
            By.cssSelector(".cart_total");

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
                        viewCartLink
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

    // Them san pham vao gio hang
    private void addProduct(String productId, String quantity) {
        driver.get(url + "product_details/" + productId);

        WebElement quantityBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        quantityInput
                )
        );

        quantityBox.clear();
        quantityBox.sendKeys(quantity);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        addToCartButton
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        cartModal
                )
        );
    }

    // Mo gio hang tu modal
    private void openCartFromModal() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        modalViewCartLink
                )
        ).click();

        if (driver.getCurrentUrl().contains("#google_vignette")) {
            driver.get(url + "view_cart");
        }

        wait.until(
                ExpectedConditions.urlContains("view_cart")
        );
    }

    // TC11: Them mot san pham vao gio hang
    @Test
    public void addToCart_Success() {
        addProduct("1", "1");
        openCartFromModal();

        WebElement product = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        product1Row
                )
        );

        Assert.assertTrue(
                product.isDisplayed(),
                "San pham khong hien thi trong gio hang!"
        );

        Assert.assertTrue(
                product.getText().contains("Blue Top"),
                "Ten san pham trong gio hang khong dung!"
        );
    }

    // TC12: Them hai san pham vao gio hang
    @Test
    public void addMultipleProducts_Success() {
        addProduct("1", "1");
        addProduct("2", "1");
        openCartFromModal();

        Assert.assertTrue(
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                product1Row
                        )
                ).isDisplayed(),
                "San pham 1 khong hien thi trong gio hang!"
        );

        Assert.assertTrue(
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                product2Row
                        )
                ).isDisplayed(),
                "San pham 2 khong hien thi trong gio hang!"
        );

        Assert.assertEquals(
                driver.findElements(cartRows).size(),
                2,
                "So luong dong san pham trong gio hang khong bang 2!"
        );
    }

    // TC13: Kiem tra so luong san pham
    @Test
    public void checkQuantity_Success() {
        addProduct("1", "4");
        openCartFromModal();

        WebElement quantity = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        product1Quantity
                )
        );

        Assert.assertEquals(
                quantity.getText().trim(),
                "4",
                "So luong san pham khong khop voi so luong da chon!"
        );
    }

    // TC14: Xoa san pham khoi gio hang
    @Test
    public void removeProduct_Success() {
        addProduct("1", "1");
        openCartFromModal();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        product1DeleteButton
                )
        ).click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        product1Row
                )
        );

        Assert.assertEquals(
                driver.findElements(product1Row).size(),
                0,
                "San pham van con ton tai sau khi xoa!"
        );
    }

    // TC15: Kiem tra gia va tong tien
    @Test
    public void checkPriceAndTotal_Success() {
        addProduct("1", "2");
        openCartFromModal();

        WebElement row = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        product1Row
                )
        );

        String priceText = row.findElement(cartPrice).getText();
        String quantityText = row.findElement(product1Quantity).getText();
        String totalText = row.findElement(cartTotal).getText();

        int price = Integer.parseInt(
                priceText.replaceAll("[^0-9]", "")
        );

        int quantity = Integer.parseInt(
                quantityText.replaceAll("[^0-9]", "")
        );

        int total = Integer.parseInt(
                totalText.replaceAll("[^0-9]", "")
        );

        Assert.assertEquals(
                quantity,
                2,
                "So luong san pham khong dung!"
        );

        Assert.assertEquals(
                total,
                price * quantity,
                "Tong tien khong bang don gia nhan voi so luong!"
        );
    }
}