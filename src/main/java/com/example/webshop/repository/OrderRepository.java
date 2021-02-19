package com.example.webshop.repository;

import com.example.webshop.models.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//I would have used EntityManager implementation but since this is simple application JpaRepository will do.
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
