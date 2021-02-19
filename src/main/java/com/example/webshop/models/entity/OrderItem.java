package com.example.webshop.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "order_item")
public class OrderItem {

    /*
    This is not most optimized way of generating ID's. We could have cached Sequences calls.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /*DDL on delete cascade should be used on Foreign key. This is most efficient mapping for OneToOne.
     * If classic way of mapping is used then orphanRemoval will do the trick but that would crate more Queries that are not needed.
     * Why is this OneToOne not ManyToOne? Why 1 Product can't have multiple orderItems?
     */
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    private Product product;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    private Long quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return id.equals(orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static final class OrderItemBuilder {
        private Long id;
        private Product product;
        private Order order;
        private Long quantity;

        private OrderItemBuilder() {
        }

        public static OrderItemBuilder OrderItem() {
            return new OrderItemBuilder();
        }

        public OrderItemBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public OrderItemBuilder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public OrderItemBuilder withOrder(Order order) {
            this.order = order;
            return this;
        }

        public OrderItemBuilder withQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItem build() {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(id);
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(quantity);
            return orderItem;
        }
    }
}
