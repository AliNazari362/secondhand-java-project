package com.secondhand.repository;

import com.secondhand.entity.Product;
import com.secondhand.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategory(Category category);

    List<Product> findByBrand(String brand);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<Product> findByStateOfProduct(Product.ProductState state);
}