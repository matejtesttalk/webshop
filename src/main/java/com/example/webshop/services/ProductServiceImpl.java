package com.example.webshop.services;

import com.example.webshop.models.dto.ProductDTO;
import com.example.webshop.models.entity.Product;
import com.example.webshop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private ProductRepository productRepository;


    @Autowired
    ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(Long id) {
        try {
            return productRepository.findById(id).get();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "There is no Product with id value of : " + id);
        }
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public HttpStatus deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
            return HttpStatus.ACCEPTED;
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete Product that is being used by Order.");
        }
    }

    public URI createProduct(ProductDTO product) {
        Product savedProduct = productRepository.save(product.fromDTO());
        return URI.create("localhost:8080/product/get/" + savedProduct.getId());
    }

}
