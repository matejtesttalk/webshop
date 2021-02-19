package com.example.webshop.models.entity;

import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Objects;

@Validated
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 10, max = 10)
    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    @Min(0)
    private BigDecimal priceHrk;

    private String description;

    @NotNull
    private Boolean isAvailable;

    public Product(Long id, @Size(min = 10, max = 10) @NotNull String code, @NotNull String name, @NotNull BigDecimal priceHrk, String description, @NotNull Boolean isAvailable) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.priceHrk = priceHrk;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceHrk() {
        return priceHrk;
    }

    public void setPriceHrk(BigDecimal priceHrk) {
        this.priceHrk = priceHrk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(code, product.code) &&
                Objects.equals(name, product.name) &&
                Objects.equals(priceHrk, product.priceHrk) &&
                Objects.equals(description, product.description) &&
                Objects.equals(isAvailable, product.isAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, priceHrk, description, isAvailable);
    }

    public static final class ProductBuilder {
        private Long id;
        private String code;
        private String name;
        private BigDecimal priceHrk;
        private String description;
        private Boolean isAvailable;

        private ProductBuilder() {
        }

        public static ProductBuilder Product() {
            return new ProductBuilder();
        }

        public ProductBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ProductBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ProductBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder withPriceHrk(BigDecimal priceHrk) {
            this.priceHrk = priceHrk;
            return this;
        }

        public ProductBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder withIsAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setId(id);
            product.setCode(code);
            product.setName(name);
            product.setPriceHrk(priceHrk);
            product.setDescription(description);
            product.setIsAvailable(isAvailable);
            return product;
        }
    }
}
