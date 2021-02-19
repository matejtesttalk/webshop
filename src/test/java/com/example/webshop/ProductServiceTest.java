package com.example.webshop;

import com.example.webshop.models.dto.ProductDTO;
import com.example.webshop.models.entity.Product;
import com.example.webshop.repository.ProductRepository;
import com.example.webshop.services.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/*
This test should be used with TestContainers since it would be better to use real DB instead of H2 database.
https://www.testcontainers.org/
But since task didn't mention TestContainers and I didn't know if it would be accepted so I didn't introduce it.
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void productRepository_save_ok() {
        //given
        ProductDTO productDTO = createDto(1l);

        //when
        productService.createProduct(productDTO);

        //then
        Product product = productRepository.findById(1L).get();
        Product expectedProduct = productDTO.fromDTO();
        Assert.assertNotNull(product);
        Assert.assertEquals(product.getCode(), expectedProduct.getCode());
        Assert.assertEquals(product.getDescription(), expectedProduct.getDescription());
        Assert.assertEquals(product.getIsAvailable(), expectedProduct.getIsAvailable());
        Assert.assertEquals(product.getName(), expectedProduct.getName());
    }

    /*
    This is not checking validation since validation is checked before request gets to controller.
     */
    @Test(expected = TransactionSystemException.class)
    public void productRepository_save_delete() {
        //given
        ProductDTO productDTO = new ProductDTO();

        //when
        productService.createProduct(productDTO);
    }

    @Test
    public void productRepository_read_ok() {
        //given
        ProductDTO productDTO = createDto(1l);
        productRepository.save(productDTO.fromDTO());

        //when
        Product product = productService.getProduct(productDTO.getId());

        //then
        Product expectedProduct = productDTO.fromDTO();
        Assert.assertNotNull(product);
        Assert.assertEquals(product.getCode(), expectedProduct.getCode());
        Assert.assertEquals(product.getDescription(), expectedProduct.getDescription());
        Assert.assertEquals(product.getIsAvailable(), expectedProduct.getIsAvailable());
        Assert.assertEquals(product.getName(), expectedProduct.getName());
    }

    @Test(expected = ResponseStatusException.class)
    public void productRepository_read_fail() {
        //when
        Product product = productService.getProduct(1L);
    }

    @Test
    public void productRepository_read_all_ok() {
        //given
        ProductDTO productDTO = createDto(1l);
        ProductDTO productDTO2 = createDto(2l);
        List<Product> productList = new ArrayList();
        productList.add(productDTO.fromDTO());
        productList.add(productDTO2.fromDTO());
        productRepository.saveAll(productList);

        //when
        List<Product> products = productService.getProducts();

        //then
        assertEquals(products.size(), 2);

    }

    @Test
    public void productRepository_delete_ok() {
        //given
        ProductDTO productDTO = createDto(1l);
        productRepository.save(productDTO.fromDTO());

        //when
        productService.deleteProduct(productDTO.getId());

        //then
        List<Product> products = productRepository.findAll();
        assertEquals(products.size(), 0);
    }

    @Test(expected = ResponseStatusException.class)
    public void productRepository_delete_fail() {
        //given

        //when
        productService.deleteProduct(1l);

        //then
        List<Product> products = productRepository.findAll();
        assertEquals(products.size(), 0);
    }


    private ProductDTO createDto(Long id) {
        return new ProductDTO(1L, "1234567890", "Guma", BigDecimal.ONE, "desc", true);
    }
}


