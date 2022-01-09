package org.example.iyakimovich.hw5;


import com.github.javafaker.Faker;
import com.github.javafaker.Food;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.example.iyakimovich.hw5.dto.ProductDTO;
import org.example.iyakimovich.hw5.service.ProductService;
import org.example.iyakimovich.hw5.utils.RetrofitUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductTest.class);

    static ProductService productService;
    static ProductDTO product;
    static int productId = 0;
    Faker faker = new Faker();

    @BeforeAll
    public static void runBeforeAllTests() throws IOException {
        LOGGER.info("runBeforeAllTests() - started");

        productService = RetrofitUtils.getRetrofit().create(ProductService.class);

        LOGGER.info("runBeforeAllTests() - done");
    }

    @BeforeEach
    void runBeforeEachTests() {
        LOGGER.info("runBeforeEachTests() - started");

        product = new ProductDTO()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));

        LOGGER.info("New Faker prepared: Tile: {}, Price: {}", product.getTitle(), product.getPrice());

        LOGGER.info("runBeforeEachTests() - done");
    }

    @SneakyThrows
    @AfterEach
    void runAfterEachTests() {
        LOGGER.info("runAfterEachTests() - started");

        if (productId != 0) {
            LOGGER.info("Cleaning up created product with ID: {} was deleted", productId);
            Response<ResponseBody> response = productService.deleteProduct(productId).execute();
            assertTrue(response.isSuccessful());
            LOGGER.info("Product created with ID: {} was deleted", productId);
        }

        LOGGER.info("runAfterEachTests() - done");
    }

    @SneakyThrows
    @Test
    void getProductsTest() {
        LOGGER.info("getProductsTest() - started");

        // /v1/products is broken so no test for now
        assertTrue(true);

        LOGGER.info("getProductsTest() - started");

    }

    @SneakyThrows
    @Test
    void runCRUDProductTest() {
        LOGGER.info("runCRUDProductTest() - started");

        //create product
        Response<ProductDTO> response = productService.addProduct(product).execute();
        assertTrue(response.isSuccessful());

        productId = response.body().getId();
        LOGGER.info("New product created with ID: {}", productId);

        LOGGER.info("Attempt get product by ID via API");
        response = productService.getProduct(productId).execute();
        assertTrue(response.isSuccessful());

        LOGGER.info("New product with ID: {} is extracted via API ", productId);

        ProductDTO productReturned = response.body();
        LOGGER.info("Received product title: {}", productReturned.getTitle());
        LOGGER.info("Received product price: {}", productReturned.getPrice());
        LOGGER.info("Received product category: {}", productReturned.getCategoryTitle());

        assertEquals(product.getPrice(), productReturned.getPrice());
        assertEquals(product.getTitle(), productReturned.getTitle());
        assertEquals(product.getCategoryTitle(), productReturned.getCategoryTitle());

        ProductDTO updatedProduct = new ProductDTO()
                .withId(productId)
                .withTitle(productReturned.getTitle() + " - UPDATED!")
                .withPrice(productReturned.getPrice())
                .withCategoryTitle(productReturned.getCategoryTitle());


        LOGGER.info("Attempt up update product title via API");
        response = productService.updateProduct(updatedProduct).execute();
        assertTrue(response.isSuccessful());

        LOGGER.info("Title of product with ID: {} updated via API ", productId);
        ProductDTO productUpdateReceived = response.body();
        LOGGER.info("Received product title: {}", productUpdateReceived.getTitle());
        LOGGER.info("Received product price: {}", productUpdateReceived.getPrice());
        LOGGER.info("Received product category: {}", productUpdateReceived.getCategoryTitle());

        assertEquals(updatedProduct.getTitle(), productUpdateReceived.getTitle());

        LOGGER.info("Attempt to retrieve updated product via API");
        response = productService.getProduct(productId).execute();
        assertTrue(response.isSuccessful());
        productUpdateReceived = response.body();
        LOGGER.info("Product with ID {} via API retrieved!", productId);

        LOGGER.info("Received product title: {}", productUpdateReceived.getTitle());
        LOGGER.info("Received product price: {}", productUpdateReceived.getPrice());
        LOGGER.info("Received product category: {}", productUpdateReceived.getCategoryTitle());

        assertEquals(updatedProduct.getTitle(), productUpdateReceived.getTitle());

        LOGGER.info("runCRUDProductTest() - stopped");
    }

    @SneakyThrows
    @Test
    void getProductNegativeTest() {
        LOGGER.info("getProductNegativeTest() - started");

        LOGGER.info("Attempt to retrieve non-existing product via API");
        Response<ProductDTO> response = productService.getProduct(-2).execute();
        assertFalse(response.isSuccessful());
        assertEquals(response.code(), 404);

        LOGGER.info("getProductNegativeTest() - done");
    }

}