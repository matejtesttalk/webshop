package com.example.webshop.models.dto;

import com.example.webshop.models.entity.Customer;
import com.example.webshop.models.entity.Order;
import com.example.webshop.models.entity.Status;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

//MapStruct can be used but I didn't want to use it for this sample since it is not long.
public class OrderDTO {

    private Long id;
    @NotNull
    private Long customer_id;
    @NotNull
    private List<OrderItemDTO> orderItemDTOS;
    // sincce this doesn't have setters frontend won't be able to change them. Also it is not mapped anywhere.
    private Status status;
    private BigDecimal priceHrk;
    private BigDecimal priceEur;

    public OrderDTO(Long id, Long customer_id, List<OrderItemDTO> orderItemDTOS) {
        this.id = id;
        this.customer_id = customer_id;
        this.orderItemDTOS = orderItemDTOS;
    }

    public OrderDTO() {
    }

    public static OrderDTO toOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.id = order.getId();
        orderDTO.customer_id = order.getCustomer().getId();
        orderDTO.orderItemDTOS = OrderItemDTO.toOrderItemDTOs(order.getOrderItems());
        orderDTO.status = order.getStatus();
        orderDTO.priceEur = order.getTotal_price_hrk();
        orderDTO.priceHrk = order.getTotal_price_eur();
        return orderDTO;
    }

    public Status getStatus() {
        return status;
    }

    public BigDecimal getPriceHrk() {
        return priceHrk;
    }

    public BigDecimal getPriceEur() {
        return priceEur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }

    public List<OrderItemDTO> getOrderItemDTOS() {
        return orderItemDTOS;
    }

    public void setOrderItemDTOS(List<OrderItemDTO> orderItemDTOS) {
        this.orderItemDTOS = orderItemDTOS;
    }

    public Order fromDTO(Customer customer) {
        return Order.OrderBuilder.Order()
                .withCustomer(customer).build();
    }
}
