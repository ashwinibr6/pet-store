package com.petstore.controller;

import com.petstore.dto.AnimalDTO;
import com.petstore.service.AnimalService;
import com.petstore.service.ShelterNetService;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String homePage(){
        return "Welcome to Pet Store";
    }


    @GetMapping("animals")
    public List<AnimalDTO> getAllAnimals(){
        return animalService.getAnimals();
    }



    @PostMapping("/animals")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AnimalDTO> addAnimals(@RequestBody List<Integer> animalIds){

        List<AnimalDTO> animals = shelterNetService.fetchAnimals(animalIds);
        return animalService.addAnimals(animals);
    }
}
