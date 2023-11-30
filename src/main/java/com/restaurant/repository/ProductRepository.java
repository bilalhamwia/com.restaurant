package com.restaurant.repository;

import com.restaurant.Entity.Product;
import com.restaurant.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("Select New com.restaurant.wrapper.ProductWrapper(p.id, p.name, p.description, p.price," +
            " p.status, p.category.id, p.category.name) from Product p")
    List<ProductWrapper> getAllProduct();
}
