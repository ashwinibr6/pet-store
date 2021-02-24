package com.petstore.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class StoreItemDTO {
    private Long sku;
    private String itemCategory;
    private String animalType;
    private String brand;
    private String name;
    private String description;
    private double price;
    private int quantity;

    public StoreItemDTO(Long sku, String itemCategory, String animalType, String brand,
                        String name, String description, double price) {
        this.sku = sku;
        this.itemCategory = itemCategory;
        this.animalType = animalType;
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public StoreItemDTO(Long sku, String itemCategory, String animalType, String brand,
                        String name, String description, double price, int quantity) {
        this(sku,  itemCategory,  animalType,  brand,
                 name,  description,  price);
        this.quantity = quantity;
    }


}
