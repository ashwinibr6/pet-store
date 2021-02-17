package com.petstore.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import javax.persistence.SequenceGenerator;
import java.time.LocalDate;
import java.util.Objects;

public class AnimalDTO {

    private Long id;
    private String shelternateId;
    private String animalName;
    private String species;
    private LocalDate birthDate;
    private String sex;
    private String color;

    public AnimalDTO(){

    }

//    public AnimalDTO(String shelternateId, String animalName, String species, LocalDate birthDate, String sex, String color) {
//        this.shelternateId = shelternateId;
//        this.animalName = animalName;
//        this.species = species;
//        this.birthDate = birthDate;
//        this.sex = sex;
//        this.color = color;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimalDTO animalDTO = (AnimalDTO) o;
        return Objects.equals(id, animalDTO.id) &&
                Objects.equals(shelternateId, animalDTO.shelternateId) &&
                Objects.equals(animalName, animalDTO.animalName) &&
                Objects.equals(species, animalDTO.species) &&
                Objects.equals(birthDate, animalDTO.birthDate) &&
                Objects.equals(sex, animalDTO.sex) &&
                Objects.equals(color, animalDTO.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shelternateId, animalName, species, birthDate, sex, color);
    }
}
