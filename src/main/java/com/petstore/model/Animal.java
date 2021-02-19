package com.petstore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
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
    @ElementCollection
    private List<String> bond;

    public Animal(String shelternateId, String animalName, String species, LocalDate birthDate, String sex, String color,List<String> bond) {
        this.shelternateId = shelternateId;
        this.animalName = animalName;
        this.species = species;
        this.birthDate = birthDate;
        this.sex = sex;
        this.color = color;
        if(bond==null) bond=new ArrayList<>();
        this.bond= new ArrayList<>(bond);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id) &&
                Objects.equals(shelternateId, animal.shelternateId) &&
                Objects.equals(animalName, animal.animalName) &&
                Objects.equals(species, animal.species) &&
                Objects.equals(birthDate, animal.birthDate) &&
                Objects.equals(sex, animal.sex) &&
                Objects.equals(color, animal.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shelternateId, animalName, species, birthDate, sex, color);
    }
}
