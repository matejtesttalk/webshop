package com.example.webshop.repository;

import com.example.webshop.models.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//I would have used EntityManager implementation but since this is simple application JpaRepository will do.
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
