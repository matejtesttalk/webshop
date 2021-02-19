package com.example.webshop.models.dto;

import com.example.webshop.models.entity.Product;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

//MapStruct can be used but I didn't want to use it for this sample since it is not long.
public class ProductDTO {

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
    @JsonProperty("isAvailable")
    private Boolean isAvailable;

    public ProductDTO(Long id, String code, String name, BigDecimal priceHrk, String description, Boolean isAvailable) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.priceHrk = priceHrk;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    public ProductDTO() {
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

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product fromDTO() {
        return Product.ProductBuilder
                .Product()
                .withCode(this.code)
                .withDescription(this.description)
                .withIsAvailable(this.isAvailable)
                .withName(this.name)
                .withPriceHrk(this.priceHrk).build();
    }

}
