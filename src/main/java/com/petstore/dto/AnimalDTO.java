package com.petstore.dto;

import lombok.Data;
import lombok.Value;

import java.time.LocalDate;
import java.util.Objects;

@Value
@Data
public class AnimalDTO {

    private Long id;
    private String shelternateId;
    private String animalName;
    private String species;
    private LocalDate birthDate;
    private String sex;
    private String color;

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
