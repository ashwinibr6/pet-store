package com.petstore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Getter
@Entity
public class StoreItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sku;
    private String itemCategory;
    private String animalType;
    private String brand;
    private String name;
    private String description;
    private double price;

    public StoreItem(long sku, String itemCategory, String animalType, String brand, String name, String description, double price) {
        this.sku = sku;
        this.itemCategory = itemCategory;
        this.animalType = animalType;
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
