package com.petstore.controller;

import com.petstore.POJO.AdoptionResponse;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ItemPurchaseRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.AnimalReturnDto;
import com.petstore.dto.StoreItemDTO;
import com.petstore.exception.AddAnimalException;
import com.petstore.model.Status;
import com.petstore.service.AnimalService;
import com.petstore.service.ShelterNetService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
    public List<AnimalDTO> addAnimals(@Validated @RequestBody List<Integer> animalIds) throws Exception {
        List<AnimalDTO> animals = shelterNetService.fetchAnimals(animalIds);
        try {
            return animalService.addAnimals(animals);
        } catch (Exception e) {
            throw new AddAnimalException("Animal already exists");
        }
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

    @DeleteMapping("/animals/return-request")
    @ResponseStatus(HttpStatus.OK)
    public List<AnimalReturnDto> retunRequestedAnimalToShelter(@RequestBody List<String> shelterIds) {
        return animalService.returnRequestedAnimalToShelter(shelterIds);
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

        if (adoptionRequestDTO != null
                && adoptionRequestDTO.getStatus().equals(Status.APPROVED.name()))
            shelterNetNotificationStatus = shelterNetService.notifyAnimalAdoption(adoptionRequestDTO);

        AdoptionResponse adoptionResponse = new AdoptionResponse(shelterNetNotificationStatus, adoptionRequestDTO);

        return adoptionResponse;
    }

    @PatchMapping("/bondedanimal")
    @ResponseStatus(HttpStatus.OK)
    public void bondAnimal(@RequestBody List<String> shelternateID) {
        animalService.bondAnimals(shelternateID);
    }

    @GetMapping("/animal/{shelternateID}")
    @ResponseStatus(HttpStatus.OK)
    public AnimalDTO getAnimal(@PathVariable String shelternateID) {
        return animalService.getAnimal(shelternateID);
    }

    @PostMapping("/storeCatalog/carry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StoreItemDTO carryItem(@RequestBody StoreItemDTO storeItemDTO) {
        return animalService.carryItem(storeItemDTO);
    }

    @PostMapping("/storeCatalog/add/{id}/{quantity}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StoreItemDTO carryItem(@PathVariable long id, @PathVariable int quantity) {
        return animalService.addItemQuantity(id, quantity);
    }


    @GetMapping("/items/{searchType}/{searchValue}")
    @ResponseStatus(HttpStatus.OK)
    public List<StoreItemDTO> searchAccessoriesSku(@PathVariable String searchType,
                                                   @PathVariable String searchValue) {

        return animalService.searchAccessories(searchType, searchValue);
    }

    @GetMapping("/items/{searchType}/{searchValue}/{secondSearchType}/{secondSearchValue}")
    @ResponseStatus(HttpStatus.OK)
    public List<StoreItemDTO> searchAccessories(@PathVariable String searchType,
                                                @PathVariable String searchValue,
                                                @PathVariable String secondSearchType,
                                                @PathVariable String secondSearchValue) {

        return animalService.searchAccessories(searchType, searchValue,
                secondSearchType, secondSearchValue);
    }


    @PatchMapping("/storeCatalog/purchaseItem/credit/")
    public double purchaseItemFromStoreWithCredit(@RequestBody List<ItemPurchaseRequest> itemPurchaseRequests) {
        return animalService.purchaseItemFromStoreWithCredit(itemPurchaseRequests);
    }

}
