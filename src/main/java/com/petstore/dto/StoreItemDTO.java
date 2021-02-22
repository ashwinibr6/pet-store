package com.petstore.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
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
}
