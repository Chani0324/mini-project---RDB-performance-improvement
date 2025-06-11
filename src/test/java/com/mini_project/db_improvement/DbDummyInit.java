package com.mini_project.db_improvement;

import com.mini_project.db_improvement.application.repository.CategoryRepository;
import com.mini_project.db_improvement.application.repository.OrderRepository;
import com.mini_project.db_improvement.application.repository.ProductRepository;
import com.mini_project.db_improvement.application.repository.UserRepository;
import com.mini_project.db_improvement.domain.entity.*;
import com.mini_project.db_improvement.domain.enums.OrderStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DbDummyInit {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository catRepo;

    @Autowired
    private ProductRepository prodRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Test
    @Transactional
    @Rollback(false)
    void initData() {
        // 1) Categories 500개
        List<Category> categories = IntStream.rangeClosed(1, 500)
                .mapToObj(i -> Category.builder()
                        .name("Category " + i)
                        .build())
                .toList();
        catRepo.saveAll(categories);

        // 2) Users 10,000명
        List<User> users = IntStream.rangeClosed(1, 10000)
                .mapToObj(i -> User.builder()
                        .name("User" + i)
                        .email("user" + i + "@example.com")
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();
        userRepo.saveAll(users);

        // 3) Products 10,000개
        List<Product> products = IntStream.rangeClosed(1, 10000)
                .mapToObj(i -> {
                    Category c = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));
                    return Product.builder()
                            .category(c)
                            .name("Product" + i)
                            .price(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 1000)))
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .toList();
        prodRepo.saveAll(products);

        // 4) Orders & OrderItems (30,000건 주문, 평균 3개 아이템)
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 30000; i++) {
            User u = users.get(ThreadLocalRandom.current().nextInt(users.size()));
            Order o = Order.builder()
                    .user(u)
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.NEW)
                    .build();

            int itemCount = ThreadLocalRandom.current().nextInt(1, 6);
            for (int j = 0; j < itemCount; j++) {
                Product p = products.get(ThreadLocalRandom.current().nextInt(products.size()));
                o.getItems().add(
                        OrderItem.builder()
                                .order(o)
                                .product(p)
                                .quantity(ThreadLocalRandom.current().nextInt(1, 6))
                                .unitPrice(p.getPrice())
                                .build()
                );
            }
            orders.add(o);
        }
        orderRepo.saveAll(orders);
    }
}