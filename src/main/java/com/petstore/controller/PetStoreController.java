package com.petstore.controller;

import com.petstore.POJO.CustomerRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import com.petstore.service.ShelterNetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

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
    public String homePage(){
        return "Welcome to Pet Store";
    }

    @PostMapping("/animal")
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalDTO addAnimal(@RequestBody AnimalDTO animalDTO){
        return animalService.addAnimal(animalDTO);
    }

    @GetMapping("animals")
    @ResponseStatus(HttpStatus.OK)
    public List<AnimalDTO> getAllAnimals(){
        return animalService.getAnimals();
    }



    @PostMapping("animals")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AnimalDTO> addAnimals(@RequestBody List<Integer> animalIds){

        List<AnimalDTO> animals = shelterNetService.fetchAnimals(animalIds);
        return animalService.addAnimals(animals);
    }

    @PostMapping("adopt")
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionRequestDTO adoptAnimals(@RequestBody CustomerRequest customerRequest){

        return animalService.createAdoptionRequest(customerRequest);

    }

    @DeleteMapping("/animalreturns")
    @ResponseStatus(HttpStatus.OK)
    public void returnAnimalToShelter(@RequestBody List<String> shelterIds){

        HttpStatus status=shelterNetService.returnAnimalToShelter(shelterIds);
        if(status.is2xxSuccessful()){
            animalService.removeAnimals(shelterIds);
        }

    }
}
