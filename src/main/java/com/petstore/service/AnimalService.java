package com.petstore.service;

import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.AnimalReturnDto;
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
                animalDTO.getBirthDate(), animalDTO.getSex(), animalDTO.getColor(), animalDTO.getBond(), animalDTO.getNote());
    }

    private AnimalDTO mapToDto(Animal animal) {
        return new AnimalDTO(animal.getShelternateId(), animal.getAnimalName(), animal.getSpecies(),
                animal.getBirthDate(), animal.getSex(), animal.getColor(), animal.getBond(), animal.getNote());
    }

    private AdoptionRequestDTO mapToAdoptionRequestDto(AdoptionRequest adoptionRequest) {
        List<AnimalDTO> animalDTOS =
                adoptionRequest.getAnimals().stream().map(animal -> mapToDto(animal)).collect(Collectors.toList());
        return new AdoptionRequestDTO(adoptionRequest.getClient(), animalDTOS, adoptionRequest.getStatus(),
                adoptionRequest.getComment());
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

    public AdoptionRequestDTO manageRequest(Long id, ProcessAdoptionRequest processAdoptionRequest) {
        AdoptionRequest adoptionRequest = adoptionRequestRepository.getOne(id);
        AdoptionRequestDTO adoptionRequestDTO = mapToAdoptionRequestDto(adoptionRequest);
        boolean allInseparable = true;

        if (adoptionRequest != null) {
            List<String> shelterNetIds = adoptionRequest.getAnimals().stream()
                    .map(animal -> animal.getShelternateId())
                    .collect(Collectors.toList());
            for (Animal animal : adoptionRequest.getAnimals()) {
                if (animal.getBond().size() > 0) {
                    List<String> bond = animal.getBond();
                    if (!shelterNetIds.containsAll(bond)) {
                        allInseparable = false;
                        break;
                    }
                }
            }

            if (allInseparable) {
                adoptionRequest.setStatus(processAdoptionRequest.getStatus());
                adoptionRequest.setComment(processAdoptionRequest.getComment());
                adoptionRequestDTO = mapToAdoptionRequestDto(adoptionRequestRepository.save(adoptionRequest));

                if (adoptionRequest.getStatus().equals(Status.APPROVED.name())) {
                    removeAnimals(shelterNetIds);
                }
            } else {
                adoptionRequest.setStatus(Status.DENIED.name());
                adoptionRequest.setComment("Denied, Can't be adopted");
                adoptionRequestDTO = mapToAdoptionRequestDto(adoptionRequestRepository.save(adoptionRequest));
            }
        }
        return adoptionRequestDTO;
    }

    public void bondAnimals(List<String> bond) {
        for (String id : bond) {
            Animal animal = animalRepository.findByShelternateId(id);
            animal.setBond(bond.stream().filter(shelterId -> !shelterId.equals(id)).collect(Collectors.toList()));
            animalRepository.save(animal);
        }
    }

    public List<AnimalReturnDto> returnRequestedAnimalToShelter(List<String> shelterIds) {
        List<Animal> animals = shelterIds.stream().map(id -> animalRepository.findByShelternateId(id)).collect(Collectors.toList());
        shelterIds.stream().forEach(id -> animalRepository.deleteAnimalByShelternateId(id));

        return animals.stream().map(animal -> new AnimalReturnDto(animal.getShelternateId(), animal.getNote())).collect(Collectors.toList());
    }
}
