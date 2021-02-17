package com.petstore.repository;

import com.petstore.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal,Long> {
    void deleteAnimalByShelternateId(String shelterId);
    List<Animal> findAllByIsSick(Boolean isSick);
}
