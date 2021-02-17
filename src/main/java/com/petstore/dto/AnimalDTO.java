package com.petstore.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnimalDTO {

    private Long id;
    private String shelternateId;
    private String animalName;
    private String species;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDate;
    private String sex;
    private String color;

    private Boolean isSick;
    private String diagnose;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimalDTO animalDTO = (AnimalDTO) o;
        return Objects.equals(shelternateId, animalDTO.shelternateId) &&
                Objects.equals(animalName, animalDTO.animalName) &&
                Objects.equals(species, animalDTO.species) &&
                Objects.equals(birthDate, animalDTO.birthDate) &&
                Objects.equals(sex, animalDTO.sex) &&
                Objects.equals(color, animalDTO.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelternateId, animalName, species, birthDate, sex, color);
    }
}
