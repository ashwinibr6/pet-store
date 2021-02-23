package com.petstore.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Getter
@Entity
@Setter
@EqualsAndHashCode
@NoArgsConstructor
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
    private int quantity;


    public StoreItem(long sku, String itemCategory, String animalType, String brand, String name, String description, double price) {
        this.sku = sku;
        this.itemCategory = itemCategory;
        this.animalType = animalType;
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public StoreItem(long sku, String itemCategory, String animalType, String brand, String name, String description, double price, int quantity) {
        this(sku,  itemCategory,  animalType,  brand,  name,  description,  price);
        this.quantity = quantity;
    }

}
