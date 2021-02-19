package com.example.webshop.routes;

import com.example.webshop.models.dto.ProductDTO;
import com.example.webshop.models.entity.Product;
import com.example.webshop.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/*
ExceptionHandlers could be made to catch exceptions and handle but for sake of simplicity I didn't introduce them.
 */
@RestController()
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/get/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/get")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus deleteProduct(@PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

    @PostMapping("/create")
    public URI createProduct(@Valid @RequestBody ProductDTO product) {
        return productService.createProduct(product);
    }
}
