package com.petstore.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter

public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String client;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Animal> animals ;

    private String status;

    public AdoptionRequest(String client, List<Animal> animals, String status) {
        this.client = client;
        this.animals = animals;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdoptionRequest that = (AdoptionRequest) o;
        return Objects.equals(client, that.client) && Objects.equals(animals, that.animals) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, animals, status);
    }
}
