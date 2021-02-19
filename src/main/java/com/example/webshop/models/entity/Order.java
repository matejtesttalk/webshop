package com.example.webshop.models.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webshop_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    private Status status;
    private BigDecimal total_price_hrk;
    private BigDecimal total_price_eur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getTotal_price_hrk() {
        return total_price_hrk;
    }

    public void setTotal_price_hrk(BigDecimal total_price_hrk) {
        this.total_price_hrk = total_price_hrk;
    }

    public BigDecimal getTotal_price_eur() {
        return total_price_eur;
    }

    public void setTotal_price_eur(BigDecimal total_price_eur) {
        this.total_price_eur = total_price_eur;
    }


    public static final class OrderBuilder {
        private Long id;
        private Customer customer;
        private List<OrderItem> orderItems = new ArrayList<>();
        private Status status;
        private BigDecimal total_price_hrk;
        private BigDecimal total_price_eur;

        private OrderBuilder() {
        }

        public static OrderBuilder Order() {
            return new OrderBuilder();
        }

        public OrderBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public OrderBuilder withCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public OrderBuilder withOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public OrderBuilder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public OrderBuilder withTotal_price_hrk(BigDecimal total_price_hrk) {
            this.total_price_hrk = total_price_hrk;
            return this;
        }

        public OrderBuilder withTotal_price_eur(BigDecimal total_price_eur) {
            this.total_price_eur = total_price_eur;
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setId(id);
            order.setCustomer(customer);
            order.setOrderItems(orderItems);
            order.setStatus(status);
            order.setTotal_price_hrk(total_price_hrk);
            order.setTotal_price_eur(total_price_eur);
            return order;
        }
    }
}
