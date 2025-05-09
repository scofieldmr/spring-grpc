package com.productservice.repository;

import com.productservice.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {

    Optional<Products> findByName(String name);
}
