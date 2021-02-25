package com.petstore.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FetchReturnAnimalsDTO {

    private int id;
    private String name;
    private String species;
    private String birthDate;
    private String sex;
    private String color;
    private String notes;
}
