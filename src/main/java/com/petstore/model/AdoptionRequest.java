package com.petstore.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter

public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String client;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Animal> animals ;

    public AdoptionRequest(String client, List<Animal> animals) {
        this.client = client;
        this.animals = animals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdoptionRequest that = (AdoptionRequest) o;

        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        return animals != null ? animals.equals(that.animals) : that.animals == null;
    }

    @Override
    public int hashCode() {
        int result = client != null ? client.hashCode() : 0;
        result = 31 * result + (animals != null ? animals.hashCode() : 0);
        return result;
    }
}
