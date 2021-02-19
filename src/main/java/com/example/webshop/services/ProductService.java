package com.example.webshop.services;

import com.example.webshop.models.dto.ProductDTO;
import com.example.webshop.models.entity.Product;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;

public interface ProductService {

    public Product getProduct(Long id);

    public List<Product> getProducts();

    public HttpStatus deleteProduct(Long id);

    public URI createProduct(ProductDTO product);
}
