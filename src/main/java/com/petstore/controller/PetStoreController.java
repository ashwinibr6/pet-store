package com.petstore.controller;

import com.petstore.POJO.AdoptionResponse;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.StoreItemDTO;
import com.petstore.model.Status;
import com.petstore.model.StoreItem;
import com.petstore.service.AnimalService;
import com.petstore.service.ShelterNetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class PetStoreController {

    private AnimalService animalService;

    private ShelterNetService shelterNetService;

    public PetStoreController(AnimalService animalService, ShelterNetService shelterNetService) {
        this.animalService = animalService;
        this.shelterNetService = shelterNetService;
    }

    @GetMapping("home")
    public String homePage() {
        return "Welcome to Pet Store";
    }

    @GetMapping("animals")
    @ResponseStatus(HttpStatus.OK)
    public List<AnimalDTO> getAllAnimals() {
        return animalService.getAnimals();
    }

    @PostMapping("animals")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AnimalDTO> addAnimals(@RequestBody List<Integer> animalIds) {
        List<AnimalDTO> animals = shelterNetService.fetchAnimals(animalIds);
        return animalService.addAnimals(animals);
    }

    @PostMapping("adopt")
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionRequestDTO adoptAnimals(@RequestBody CustomerRequest customerRequest) {
        return animalService.createAdoptionRequest(customerRequest);
    }

    @DeleteMapping("/animalreturns")
    @ResponseStatus(HttpStatus.OK)
    public void returnAnimalToShelter(@RequestBody List<String> shelterIds) {
        HttpStatus status = shelterNetService.returnAnimalToShelter(shelterIds);
        if (status.is2xxSuccessful()) {
            animalService.removeAnimals(shelterIds);
        }
    }

    @DeleteMapping("/sickanimal")
    @ResponseStatus(HttpStatus.OK)
    public void returnSickAnimalToShelter(@RequestParam String shelternateId, @RequestParam String diagnosis) {
        if (animalService.getAnimal(shelternateId) != null) {
            HttpStatus status = shelterNetService.returnSickAnimalToShelter(shelternateId, diagnosis);
            if (status.is2xxSuccessful()) {
                animalService.removeAnimals(List.of(shelternateId));
            }
        }
    }

    @PutMapping("adopt/request/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdoptionResponse processAdoptionRequest(@PathVariable Long id, @RequestBody ProcessAdoptionRequest processAdoptionRequest) {
        HttpStatus shelterNetNotificationStatus = null;
        AdoptionRequestDTO adoptionRequestDTO = animalService.manageRequest(id, processAdoptionRequest);

        if(adoptionRequestDTO != null
                && adoptionRequestDTO.getStatus().equals(Status.APPROVED.name()))
            shelterNetNotificationStatus = shelterNetService.notifyAnimalAdoption(adoptionRequestDTO);

        AdoptionResponse adoptionResponse = new AdoptionResponse(shelterNetNotificationStatus, adoptionRequestDTO);

        return adoptionResponse;
    }

    @PatchMapping("/bondedanimal")
    @ResponseStatus(HttpStatus.OK)
    public void bondAnimal(@RequestBody List<String> shelternateID){
        animalService.bondAnimals(shelternateID);
    }

    @GetMapping("/animal/{shelternateID}")
    @ResponseStatus(HttpStatus.OK)
    public AnimalDTO getAnimal(@PathVariable String shelternateID){
        return animalService.getAnimal(shelternateID);
    }

    @PostMapping("/storeCatalog/carry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StoreItemDTO carryItem(@RequestBody StoreItem storeItem){
        return animalService.carryItem(storeItem);
    }
}
