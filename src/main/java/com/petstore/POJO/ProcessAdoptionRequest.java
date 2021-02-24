package com.petstore.POJO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProcessAdoptionRequest {

    private String status;
    private String comment;
}
