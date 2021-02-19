package com.example.webshop.routes;

import com.example.webshop.models.dto.OrderDTO;
import com.example.webshop.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

/*
ExceptionHandlers could be made to catch exceptions and handle but for sake of simplicity I didn't introduce them.
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;

    @Autowired
    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("read-order/{id}")
    public OrderDTO getOrder(@PathVariable("id") Long id) {
        return this.orderService.getOrder(id);
    }

    @PostMapping("create-order")
    public URI createOrder(@RequestBody @Valid OrderDTO order) {
        return this.orderService.crateOrder(order);
    }

    @PutMapping("update-order")
    public URI updateOrder(@RequestBody @Valid OrderDTO order) {
        return orderService.updateOrder(order);
    }

    @GetMapping("delete-order/{id}")
    public HttpStatus deleteOrder(@PathVariable("id") Long id) {
        return this.orderService.deleteOrder(id);
    }

    @GetMapping("finalize-order/{id}")
    public URI finalizeOrder(@PathVariable("id") Long id) {
        return orderService.finalizeOrder(id);
    }

}
