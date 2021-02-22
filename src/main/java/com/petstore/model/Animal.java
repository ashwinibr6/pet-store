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
    private String note;

    public Animal(String shelternateId, String animalName, String species, LocalDate birthDate, String sex, String color,List<String> bond,String note) {
        this.shelternateId = shelternateId;
        this.animalName = animalName;
        this.species = species;
        this.birthDate = birthDate;
        this.sex = sex;
        this.color = color;
        if(bond==null) bond=new ArrayList<>();
        this.bond= new ArrayList<>(bond);
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animal)) return false;
        Animal animal = (Animal) o;
        return Objects.equals(getId(), animal.getId()) &&
                Objects.equals(getShelternateId(), animal.getShelternateId()) &&
                Objects.equals(getAnimalName(), animal.getAnimalName()) &&
                Objects.equals(getSpecies(), animal.getSpecies()) &&
                Objects.equals(getBirthDate(), animal.getBirthDate()) &&
                Objects.equals(getSex(), animal.getSex()) &&
                Objects.equals(getColor(), animal.getColor()) &&
                Objects.equals(getBond(), animal.getBond()) &&
                Objects.equals(getNote(), animal.getNote());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getShelternateId(), getAnimalName(), getSpecies(), getBirthDate(), getSex(), getColor(), getBond(), getNote());
    }
}
