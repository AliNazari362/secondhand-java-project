package com.secondhand.repository;

import com.secondhand.entity.Product;
import com.secondhand.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * derived query methods for filtering products by category, brand, price range, and
 * physical condition.</p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Returns all products belonging to the specified category.
     *
     * @param category the {@link Category} to filter by
     * @return list of matching products; empty list if none found
     */
    List<Product> findByCategory(Category category);

    /**
     * Returns all products matching the specified brand name.
     *
     * @param brand the brand name to search for (exact match)
     * @return list of matching products; empty list if none found
     */
    List<Product> findByBrand(String brand);

    /**
     * Returns all products whose price falls within the given inclusive range.
     *
     * @param min the minimum price (inclusive)
     * @param max the maximum price (inclusive)
     * @return list of products within the price range; empty list if none found
     */
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    /**
     * Returns all products in the specified physical condition.
     *
     * @param state the {@link Product.ProductState} to filter by (e.g., NEW, GOOD, DAMAGED)
     * @return list of matching products; empty list if none found
     */
    List<Product> findByStateOfProduct(Product.ProductState state);
}
