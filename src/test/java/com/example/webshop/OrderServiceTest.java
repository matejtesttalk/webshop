package com.example.webshop;

import com.example.webshop.models.dto.OrderDTO;
import com.example.webshop.models.dto.OrderItemDTO;
import com.example.webshop.models.entity.*;
import com.example.webshop.repository.CustomerRepository;
import com.example.webshop.repository.OrderItemRepository;
import com.example.webshop.repository.OrderRepository;
import com.example.webshop.repository.ProductRepository;
import com.example.webshop.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

/*
This test should be used with TestContainers since it would be better to use real DB instead of H2 database.
https://www.testcontainers.org/
But since task didn't mention TestContainers and I didn't know if it would be accepted so I didn't introduce it.
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderServiceTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(12259);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassLoader classLoader = getClass().getClassLoader();
    private final File file = new File(classLoader.getResource("Order.json").getFile());
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    public void order_read_ok() {
        //given
        Customer customer = createCustomer();
        Order orderSave = createOrder();
        customerRepository.saveAndFlush(customer);
        Long id = orderRepository.saveAndFlush(orderSave).getId();
        List<Order> orders = orderRepository.findAll();

        //when
        OrderDTO orderReturned = orderService.getOrder(id);

        //then
        Assert.assertNotNull(orderReturned);
        Assert.assertEquals(orders.size(), 1);
    }

    @Test
    public void order_delete_ok() {
        //given
        Customer customer = createCustomer();
        Order orderSave = createOrder();
        customerRepository.saveAndFlush(customer);
        Long id = orderRepository.saveAndFlush(orderSave).getId();

        //when
        HttpStatus httpStatus = orderService.deleteOrder(id);

        //then
        List<Order> orders = orderRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        Assert.assertEquals(httpStatus, HttpStatus.ACCEPTED);
        Assert.assertEquals(orders.size(), 0);
        Assert.assertEquals(customers.size(), 1);
    }


    @Test
    public void order_create_ok() {
        //given
        Customer customer = createCustomer();
        Long customerID = customerRepository.saveAndFlush(customer).getId();
        Product product = createProduct(1L);
        Long producId = productRepository.saveAndFlush(product).getId();
        OrderDTO orderDTO = createOrderDTO(createOrder());
        orderDTO.setCustomer_id(customerID);
        orderDTO.getOrderItemDTOS().get(0).setProduct_id(producId);

        //when
        URI uri = orderService.crateOrder(orderDTO);

        //then
        List<Order> orders = orderRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        Assert.assertEquals(orders.size(), 1);
        Assert.assertEquals(customers.size(), 1);
    }

    @Test
    public void order_update_ok() {
        //given
        Long customerID = saveCustomer();
        Long productId = saveProduct().getId();
        Long producId = saveProduct().getId();
        Order order = orderRepository.saveAndFlush(createOrder());

        OrderDTO orderDTO = createOrderDTO(createOrder());
        orderDTO.setCustomer_id(customerID);
        orderDTO.setId(order.getId());
        orderDTO.getOrderItemDTOS().get(0).setProduct_id(producId);
        orderDTO.getOrderItemDTOS().get(0).setId(order.getOrderItems().get(0).getId());
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setProduct_id(productId);
        orderDTO.getOrderItemDTOS().add(orderItemDTO);

        List<OrderItem> orderItems = orderItemRepository.findAll();
        Assert.assertEquals(orderItems.size(), 1);

        //when
        URI uri = orderService.updateOrder(orderDTO);

        //then
        List<Order> orders = orderRepository.findAll();
        orderItems = orderItemRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        Assert.assertEquals(orderItems.size(), 2);
        Assert.assertEquals(orders.size(), 1);
        Assert.assertNotNull(uri);
        Assert.assertEquals(customers.size(), 1);
    }

    @Test
    public void order_update_delete_order_item_ok() {
        //given
        Long customerID = saveCustomer();
        Long productId = saveProduct().getId();
        Order order = createOrder();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(saveProduct());
        orderItem.setQuantity(20L);
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);
        order = orderRepository.saveAndFlush(order);

        OrderDTO orderDTO = createOrderDTO(createOrder());
        orderDTO.setCustomer_id(customerID);
        orderDTO.setId(order.getId());
        orderDTO.getOrderItemDTOS().get(0).setProduct_id(productId);
        orderDTO.getOrderItemDTOS().get(0).setId(order.getOrderItems().get(0).getId());

        List<OrderItem> orderItems = orderItemRepository.findAll();
        Assert.assertEquals(orderItems.size(), 2);

        //when
        URI uri = orderService.updateOrder(orderDTO);

        //then
        List<Order> orders = orderRepository.findAll();
        orderItems = orderItemRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        Assert.assertEquals(orderItems.size(), 1);
        Assert.assertEquals(orders.size(), 1);
        Assert.assertNotNull(uri);
        Assert.assertEquals(customers.size(), 1);
    }


    @Transactional
    @Test
    public void finalize_ok() {
        //given
        wireMockRule.stubFor(get(urlPathMatching("/.*")).willReturn(WireMock.aResponse().withHeader("Content-Type", APPLICATION_JSON).withBody(HNBPojoJson())));
        saveCustomer();
        saveProduct();
        Order order = orderRepository.saveAndFlush(createOrder());

        //when
        URI uri = orderService.finalizeOrder(order.getId());

        //then
        order = orderRepository.findById(order.getId()).get();
        Assert.assertNotNull(uri);
        Assert.assertEquals(order.getStatus(), Status.SUBMITTED);
        Assert.assertNotNull(order.getTotal_price_eur());
        Assert.assertNotNull(order.getTotal_price_hrk());
        Assert.assertNotEquals(order.getTotal_price_hrk(), order.getTotal_price_eur());

        Assert.assertEquals(order.getTotal_price_hrk(), order.getOrderItems()
                .stream()
                .map(orderItem -> orderItem.getProduct().getPriceHrk()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Assert.assertEquals(order.getTotal_price_eur(), order.getOrderItems()
                .stream()
                .map(orderItem ->
                        orderItem.getProduct().getPriceHrk()
                                .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal("7.566015"), RoundingMode.HALF_UP));
    }

    //ReadTimeoutException is thrown but since we are catching we will throw ResponseStatusException!
    @Transactional
    @Test(expected = ResponseStatusException.class)
    public void finalize_timeout_fail() {
        //given
        wireMockRule.stubFor(get(urlPathMatching("/.*")).willReturn(WireMock.aResponse().withFixedDelay(20000).withHeader("Content-Type", APPLICATION_JSON).withBody(HNBPojoJson())));
        saveCustomer();
        saveProduct();
        Order order = orderRepository.saveAndFlush(createOrder());

        //when
        URI uri = orderService.finalizeOrder(order.getId());

    }


    private OrderDTO createOrderDTO(Order order) {
        return OrderDTO.toOrderDTO(order);
    }

    public Order createOrder() {
        try {
            Order order = objectMapper.readValue(file, Order.class);
            order.getOrderItems().get(0).setOrder(order);
            return order;
        } catch (Throwable t) {
            return null;
        }
    }


    public String HNBPojoJson() {
        return "[{\"broj_tecajnice\":\"32\",\"datum_primjene\":\"2021-02-17\",\"drzava\":\"EMU\",\"drzava_iso\":\"EMU\",\"sifra_valute\":\"978\",\"valuta\":\"EUR\",\"jedinica\":1,\"kupovni_tecaj\":\"7,543317\",\"srednji_tecaj\":\"7,566015\",\"prodajni_tecaj\":\"7,588713\"}]";
    }

    public Customer createCustomer() {
        return Customer.CustomerBuilder.aCustomer()
                .withId(1l)
                .withFirstName("testName")
                .withLastName("testSurname")
                .withEmail("test@test.com").build();
    }

    private Product createProduct(Long id) {
        return new Product(1L, "1234567890", "Guma", BigDecimal.ONE, "desc", true);
    }

    private Long saveCustomer() {
        Customer customer = createCustomer();
        return customerRepository.saveAndFlush(customer).getId();
    }

    private Product saveProduct() {
        Product product = createProduct(1L);
        return productRepository.saveAndFlush(product);
    }

}
