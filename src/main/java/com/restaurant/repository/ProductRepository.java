package com.restaurant.repository;

import com.restaurant.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Integer> {
}
