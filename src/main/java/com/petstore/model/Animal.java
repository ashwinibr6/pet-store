package com.petstore.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String shelternateId;
    private String animalName;
    private String species;
    private LocalDate birthDate;
    private String sex;
    private String color;

    public Animal(String shelternateId, String animalName, String species, LocalDate birthDate, String sex, String color) {
        this.shelternateId = shelternateId;
        this.animalName = animalName;
        this.species = species;
        this.birthDate = birthDate;
        this.sex = sex;
        this.color = color;
    }
}
