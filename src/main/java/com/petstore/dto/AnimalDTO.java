package com.petstore.dto;

import lombok.Data;
import lombok.Value;

import java.time.LocalDate;

@Value
@Data
public class AnimalDTO {

    private String shelternateId;
    private String animalName;
    private String species;
    private LocalDate birthDate;
    private String sex;
    private String color;

}
