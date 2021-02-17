package com.petstore.service;

import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.petstore.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    private Animal mapTo(AnimalDTO animalDTO) {
        return new Animal(animalDTO.getShelternateId(), animalDTO.getAnimalName(), animalDTO.getSpecies(),
                animalDTO.getBirthDate(), animalDTO.getSex(), animalDTO.getColor());
    }

    private AnimalDTO mapToDto(Animal animal) {
        return new AnimalDTO(animal.getId(),animal.getShelternateId(), animal.getAnimalName(), animal.getSpecies(),
                animal.getBirthDate(), animal.getSex(), animal.getColor());
    }

    public AnimalDTO addAnimal(AnimalDTO animalDTO) {
        Animal animal = animalRepository.save(mapTo(animalDTO));
        return mapToDto(animal);
    }

    public List<AnimalDTO> getAnimals() {
        return animalRepository.findAll().stream().map(animal -> mapToDto(animal)).collect(Collectors.toList());
    }

    public List<AnimalDTO> addAnimals(List<AnimalDTO> animals) {

        List<Animal> animalList = new ArrayList<>();
        for(AnimalDTO animalDto : animals){
            animalList.add(mapTo(animalDto));
        }
        animalList = animalRepository.saveAll(animalList);

        List<AnimalDTO> animalsDtos = new ArrayList<>();
        for(Animal animal : animalList){
            animalsDtos.add(mapToDto(animal));
        }

        return animalsDtos;
    }


}
