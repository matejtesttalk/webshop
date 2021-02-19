package com.example.webshop.models.dto;

import com.example.webshop.models.entity.Order;
import com.example.webshop.models.entity.OrderItem;
import com.example.webshop.models.entity.Product;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

//MapStruct can be used but I didn't want to use it for this sample since it is not big.
public class OrderItemDTO {

    private Long id;
    // Just question for response do we need to return products? Or frontend alrdy has it?
    @NotNull
    private Long product_id;
    @NotNull
    private Long quantity;

    public static OrderItem fromDTO(OrderItemDTO orderItemDTO, Product product, Order order) {
        return OrderItem.OrderItemBuilder.OrderItem()
                .withId(orderItemDTO.id)
                .withQuantity(orderItemDTO.quantity)
                .withOrder(order)
                .withProduct(product)
                .build();
    }

    public static List<OrderItemDTO> toOrderItemDTOs(List<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItemDTO::toOrderItemDTO).collect(Collectors.toList());
    }

    public static OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.id = orderItem.getId();
        orderItemDTO.product_id = orderItem.getProduct().getId();
        orderItemDTO.quantity = orderItem.getQuantity();
        return orderItemDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
