package com.petstore.POJO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemPurchaseRequest {
    private Long sku;
    private int quantity;
}
