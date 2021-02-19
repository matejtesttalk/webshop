package com.example.webshop.services;

import com.example.webshop.models.dto.OrderDTO;
import com.example.webshop.models.dto.OrderItemDTO;
import com.example.webshop.models.entity.*;
import com.example.webshop.models.response.HNBPojo;
import com.example.webshop.repository.CustomerRepository;
import com.example.webshop.repository.OrderRepository;
import com.example.webshop.repository.ProductRepository;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    @Autowired
    @Qualifier("hnbWebClient")
    private WebClient hnbWebClient;

    @Autowired
    OrderServiceImpl(CustomerRepository customerRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public URI crateOrder(OrderDTO orderDTO) {
        try {
            Optional<Customer> customer = this.customerRepository.findById(orderDTO.getCustomer_id());
            if (!customer.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer Not Found");
            }
            Map<Long, Product> productMap = this.productRepository.findAllByIdAndIsAvailable(orderDTO.getOrderItemDTOS().stream().map(OrderItemDTO::getProduct_id).collect(Collectors.toList())).stream().collect(Collectors.toMap(Product::getId, n -> n));
            if (productMap.size() != orderDTO.getOrderItemDTOS().size()) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Number of Products can't be lower then number of OrderItems!");
            }
            Order order = filterOrderItem(productMap, orderDTO, customer.get());
            order = orderRepository.save(order);
            /*properties for this can be used to return url so when deployed to server this doesn't need change.
             But for simplicity of task I didn't introduce it. */
            return URI.create("localhost:8080/read-order/" + order.getId());
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error has occurred");
        }
    }

    /* task didn't specify this. how does update work? Is it sending all orderItems is it only sending new ones?
       Is it removing old ones with separate list? Few questions should be asked here.
     */
    @Override
    public URI updateOrder(OrderDTO orderDTO) {
        try {
            Order order = this.orderRepository.findById(orderDTO.getId()).get();
            Map<Long, Product> productMap = this.productRepository.findAllByIdAndIsAvailable(orderDTO.getOrderItemDTOS().stream().map(OrderItemDTO::getProduct_id).collect(Collectors.toList())).stream().collect(Collectors.toMap(Product::getId, n -> n));
            Order orderToBeSaved = filterOrderItem(productMap, orderDTO, order.getCustomer());
            orderToBeSaved.setId(order.getId());
            this.orderRepository.save(orderToBeSaved);
            return URI.create("localhost:8080/read-order/" + order.getId());
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error has occurred");
        }
    }

    @Override
    public OrderDTO getOrder(Long id) {
        try {
            return OrderDTO.toOrderDTO(this.orderRepository.findById(id).get());

        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "There is no Order with id value of : " + id);
        }
    }

    @Override
    public HttpStatus deleteOrder(Long id) {
        try {
            this.orderRepository.deleteById(id);
            return HttpStatus.ACCEPTED;
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete Order that doesn't exist.");
        }
    }

    @Override
    public URI finalizeOrder(Long id) {
        //this can easily refactored to be used for reactive architecture just swap block().
        try {
            HNBPojo hnbPojo = hnbWebClient.get().uri(uriBuilder -> uriBuilder.queryParam("valuta", "EUR").build()).retrieve().bodyToMono(HNBPojo[].class).block()[0];
            Optional<Order> order = orderRepository.findById(id);
            if (order.isPresent()) {
                calculatePrice(new BigDecimal(hnbPojo.getSrednji_tecaj().replace(',', '.')), order.get());
                orderRepository.save(order.get());
            } else {
                throw new ObjectNotFoundException("Object with " + id + " is not exisisting", "Order.class");
            }
            return URI.create("localhost:8080/read-order/" + order.get().getId());
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Can't finalize Order.");
        }
    }

    // Is srednjiTecaj used here or kupovni?
    private void calculatePrice(BigDecimal srednjiTecaj, Order order) {
        order.setTotal_price_eur(BigDecimal.ZERO);
        order.setTotal_price_hrk(BigDecimal.ZERO);
        order.getOrderItems().forEach(oi -> {
            order.setTotal_price_hrk(order.getTotal_price_hrk().add(oi.getProduct().getPriceHrk().multiply(new BigDecimal(oi.getQuantity()))));
            order.setTotal_price_eur(order.getTotal_price_eur().add(oi.getProduct().getPriceHrk().multiply(new BigDecimal(oi.getQuantity()))));
        });
        order.setTotal_price_eur(order.getTotal_price_eur().divide(srednjiTecaj, RoundingMode.HALF_UP));
        order.setStatus(Status.SUBMITTED);
    }


    private Order filterOrderItem(Map<Long, Product> productMap, OrderDTO orderDTO, Customer customer) {
        Order order = orderDTO.fromDTO(customer);
        List<OrderItem> orderItems = new ArrayList<>();
        orderDTO.getOrderItemDTOS().forEach(orderItemDTO -> {
            Product product = productMap.get(orderItemDTO.getProduct_id());
            if (product.getIsAvailable().equals(false)) {
                return;
            }
            orderItems.add(OrderItemDTO.fromDTO(orderItemDTO, product, order));
        });
        order.setOrderItems(orderItems);
        order.setStatus(Status.DRAFT);
        return order;
    }
}
