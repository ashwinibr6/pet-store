package com.petstore.service;

import com.petstore.POJO.CustomerRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.model.AdoptionRequest;
import com.petstore.model.Animal;
import com.petstore.model.Status;
import com.petstore.repository.AdoptionRequestRepository;
import com.petstore.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnimalService {

    private AnimalRepository animalRepository;
    private AdoptionRequestRepository adoptionRequestRepository;

    public AnimalService(AnimalRepository animalRepository,
                         AdoptionRequestRepository adoptionRequestRepository) {
        this.animalRepository = animalRepository;
        this.adoptionRequestRepository = adoptionRequestRepository;
    }

    private Animal mapTo(AnimalDTO animalDTO) {
        return new Animal(animalDTO.getShelternateId(), animalDTO.getAnimalName(), animalDTO.getSpecies(),
                animalDTO.getBirthDate(), animalDTO.getSex(), animalDTO.getColor());
    }

    private AnimalDTO mapToDto(Animal animal) {
        return new AnimalDTO(animal.getShelternateId(), animal.getAnimalName(), animal.getSpecies(),
                animal.getBirthDate(), animal.getSex(), animal.getColor());
    }

    private AdoptionRequestDTO mapToAdoptionRequestDto(AdoptionRequest adoptionRequest) {
        List<AnimalDTO> animalDTOS =
                adoptionRequest.getAnimals().stream().map(animal -> mapToDto(animal)).collect(Collectors.toList());
        return new AdoptionRequestDTO(adoptionRequest.getClient(), animalDTOS, adoptionRequest.getStatus());
    }

    public List<AnimalDTO> getAnimals() {
        return animalRepository.findAll().stream().map(animal -> mapToDto(animal)).collect(Collectors.toList());
    }

    public List<AnimalDTO> addAnimals(List<AnimalDTO> animals) {
        List<Animal> animalList = animals.stream().map(animalDto -> mapTo(animalDto)).collect(Collectors.toList());
        animalList = animalRepository.saveAll(animalList);
        List<AnimalDTO> animalsDtos = animalList.stream().map(animal -> mapToDto(animal)).collect(Collectors.toList());
        return animalsDtos;
    }

    public AdoptionRequestDTO createAdoptionRequest(CustomerRequest customerRequest) {
        List<Animal> animals = customerRequest.getShelterNetIds()
                .stream().map(id -> animalRepository.findByShelternateId(id))
                .collect(Collectors.toList());
        AdoptionRequest adoptionRequest = new AdoptionRequest(customerRequest.getClient(), animals, Status.PENDING.name());
        return mapToAdoptionRequestDto(adoptionRequestRepository.save(adoptionRequest));
    }

    public void removeAnimals(List<String> shelterIds) {
        for (String id : shelterIds) {
            animalRepository.deleteAnimalByShelternateId(id);
        }
    }

    public AnimalDTO getAnimal(String shelternateId) {
        Animal animal = animalRepository.findByShelternateId(shelternateId);
        return mapToDto(animal);
    }
}
