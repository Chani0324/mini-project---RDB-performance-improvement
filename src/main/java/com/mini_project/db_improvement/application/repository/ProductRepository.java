package com.mini_project.db_improvement.application.repository;

import com.mini_project.db_improvement.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryIdOrderByCreatedAtDesc(long testCategoryId, Pageable pageable);
}
