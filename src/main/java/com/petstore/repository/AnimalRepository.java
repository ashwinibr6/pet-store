package com.petstore.repository;

import com.petstore.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal,Long> {

    Animal findByShelternateId(String shelternateId);
    void deleteAnimalByShelternateId(String shelternateId);
}
