package com.mini_project.db_improvement.improvement;

import com.mini_project.db_improvement.application.repository.ProductRepository;
import com.mini_project.db_improvement.domain.entity.Product;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexPerformanceTest {
    private static final int RUNS = 20;
    private static final long TEST_CATEGORY_ID = 1L;
    private static final int PAGE_SIZE = 20;
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataSource dataSource; // DDL execution

    @BeforeAll
    void setupIndex() throws SQLException {
        // Drop if exists, ignore errors
        executeDdlSilently("ALTER TABLE products DROP INDEX idx_products_category_id");
        // Create index, ignore duplicate errors
        executeDdlSilently("ALTER TABLE products ADD INDEX idx_products_category_id (category_id)");
    }

    @AfterAll
    void cleanupIndex() throws SQLException {
        executeDdlSilently("ALTER TABLE products DROP INDEX idx_products_category_id");
    }

    @Test
    void compareRepositoryMethodPerformance() {
        // Without index
        executeDdlSilently("ALTER TABLE products DROP INDEX idx_products_category_id");
        long withoutIndex = measureAverage(() -> {
            Page<Product> page = productRepository.findByCategoryIdOrderByCreatedAtDesc(
                    TEST_CATEGORY_ID,
                    PageRequest.of(PAGE_NUMBER, PAGE_SIZE)
            );
            page.getContent();
        });

        // With index
        executeDdlSilently("ALTER TABLE products ADD INDEX idx_products_category_id (category_id)");
        long withIndex = measureAverage(() -> {
            Page<Product> page = productRepository.findByCategoryIdOrderByCreatedAtDesc(
                    TEST_CATEGORY_ID,
                    PageRequest.of(PAGE_NUMBER, PAGE_SIZE)
            );
            page.getContent();
        });

        System.out.printf(
                "Average time without index: %d ms, with index: %d ms%n",
                withoutIndex, withIndex
        );
    }

    private void executeDdl(String ddl) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        }
    }

    private void executeDdlSilently(String ddl) {
        try {
            executeDdl(ddl);
        } catch (SQLException ignored) {
        }
    }

    private long measureAverage(Runnable task) {
        long totalMs = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            task.run();
            long end = System.nanoTime();
            totalMs += TimeUnit.NANOSECONDS.toMillis(end - start);
        }
        return totalMs / RUNS;
    }
}
