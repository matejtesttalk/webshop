package com.example.webshop.repository;

import com.example.webshop.models.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

//I would have used EntityManager implementation but since this is simple application JpaRepository will do.
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.id in (?1) and p.isAvailable = true")
    Collection<Product> findAllByIdAndIsAvailable(Iterable<Long> id);
}
