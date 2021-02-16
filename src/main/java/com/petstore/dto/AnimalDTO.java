package com.petstore.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
public class AnimalDTO {

    private String shelternateId;
    private String animalName;
    private String species;
    private LocalDate birthDate;
    private String sex;
    private String color;

    public AnimalDTO(String shelternateId, String animalName, String species, LocalDate birthDate, String sex, String color) {
        this.shelternateId = shelternateId;
        this.animalName = animalName;
        this.species = species;
        this.birthDate = birthDate;
        this.sex = sex;
        this.color = color;
    }

}
