package com.petstore.POJO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@AllArgsConstructor
@Getter
public class CustomerRequest {

    private String client;
    private List<String> shelterNetIds;
}
