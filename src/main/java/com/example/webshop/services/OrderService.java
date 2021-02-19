package com.example.webshop.services;

import com.example.webshop.models.dto.OrderDTO;
import org.springframework.http.HttpStatus;

import java.net.URI;

public interface OrderService {
    public URI crateOrder(OrderDTO order);

    public URI updateOrder(OrderDTO order);

    public OrderDTO getOrder(Long id);

    public HttpStatus deleteOrder(Long id);

    public URI finalizeOrder(Long id);
}
