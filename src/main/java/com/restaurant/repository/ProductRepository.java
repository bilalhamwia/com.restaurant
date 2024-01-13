package com.restaurant.repository;

import com.restaurant.Entity.Product;
import com.restaurant.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import javax.transaction.Transactional;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("Select New com.restaurant.wrapper.ProductWrapper(p.id, p.name, p.description, p.price," +
            " p.status, p.category.id, p.category.name) from Product p")
    List<ProductWrapper> getAllProduct();

    @Modifying
    @Transactional
    @Query("update Product p set p.status = :status where p.id = :id")
    Integer updateProductStatus(@Param("status") String status, @Param("id") Integer id);


    @Query("Select New com.restaurant.wrapper.ProductWrapper(p.id, p.name) from Product p where p.category.id = :id" +
    " and p.status = 'true'")
    List<ProductWrapper> getProductByCategory(@Param("id") Integer id);

    @Query("Select New com.restaurant.wrapper.ProductWrapper(p.id, p.name, p.description, p.price) from Product p where p.id = :id")
    ProductWrapper getProductById(@Param("id") Integer id);
}
