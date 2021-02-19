package com.petstore.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String client;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Animal> animals ;

    private String status;
    private String comment;

    public AdoptionRequest(String client, List<Animal> animals, String status) {
        this.client = client;
        this.animals = animals;
        this.status = status;
        this.comment = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdoptionRequest that = (AdoptionRequest) o;

        if (!client.equals(that.client)) return false;
        if (!animals.equals(that.animals)) return false;
        if (!status.equals(that.status)) return false;
        return comment != null ? comment.equals(that.comment) : that.comment == null;
    }

    @Override
    public int hashCode() {
        int result = client.hashCode();
        result = 31 * result + animals.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}
