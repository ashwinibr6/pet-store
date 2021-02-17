package com.petstore.repository;

import com.petstore.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AnimalRepository extends JpaRepository<Animal,Long> {
    void deleteAnimalByShelternateId(String shelterId);
}
