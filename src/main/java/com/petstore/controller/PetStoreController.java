package com.petstore.controller;

import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.petstore.service.AnimalService;
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

    @Autowired
    private RestTemplate restTemplate;


    public PetStoreController(AnimalService animalService) {
        this.animalService = animalService;
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
    public List<AnimalDTO> getAllAnimals(){
        return animalService.getAnimals();
    }

    @DeleteMapping("/animalreturns")
    @ResponseStatus(HttpStatus.OK)
    public void returnAnimalToShelter(@RequestBody List<String> shelterIds){
        System.out.println("inside method");
       // List<String> ids=animalDTO.stream().map(animalDTO1 -> animalDTO1.getShelternateId()).collect(Collectors.toList());
       //restTemplate.put("",shelterIds);

       animalService.removeAnimals(shelterIds);


    }
}
