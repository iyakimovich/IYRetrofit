package org.example.iyakimovich.hw5;


import lombok.SneakyThrows;
import org.example.iyakimovich.hw5.dto.CategoryDTO;
import org.example.iyakimovich.hw5.service.CategoryService;
import org.example.iyakimovich.hw5.utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTest.class);
    static CategoryService categoryService;
    static final HashMap<Integer, String> categoriesMap = new HashMap<Integer, String>();

    @BeforeAll
    public static void runBeforeAllTests() throws IOException {
        LOGGER.info("runBeforeAllTests() - started");

        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);

        categoriesMap.put(1, "Food");
        categoriesMap.put(2, "Electronic");
        categoriesMap.put(3, "Furniture");

        LOGGER.info("runBeforeAllTests() - done");
    }


    @SneakyThrows
    @Test
    void getCategoryByIdPositiveTest() {
        LOGGER.info("getCategoryByIdPositiveTest() - started");

        for (Map.Entry mapElement : categoriesMap.entrySet()) {
            int categoryID = ((Integer) mapElement.getKey()).intValue();
            String expectedCategoryName = (String) mapElement.getValue();

            Response<CategoryDTO> response = categoryService.getCategory(categoryID).execute();
            assertThat(response.isSuccessful(), CoreMatchers.is(true));

            int productCount = response.body().getProducts().size();
            String categoryName = response.body().getTitle();
            LOGGER.info("Category {} title is : {}", categoryID, categoryName);

            assertEquals(expectedCategoryName, categoryName);

            LOGGER.info("Amount of products in Category {}}: {}", categoryName, productCount);
            assertTrue(productCount > 0);
        }
    }

    @SneakyThrows
    @Test
    void getCategoryByIdNegativeTest() {
        LOGGER.info("getCategoryByIdNegativeTest() - started");

        Response<CategoryDTO> response = categoryService.getCategory(4).execute();

        boolean isSuccessful = response.isSuccessful();
        int httpCode = response.code();
        LOGGER.info("isSuccessful: {}, httpCode: {}", isSuccessful, httpCode);

        assertFalse(isSuccessful);
        assertTrue(httpCode == 404);

        LOGGER.info("getCategoryByIdNegativeTest() - stopped");
    }
}
