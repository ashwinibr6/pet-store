package com.petstore.controller;

import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.petstore.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class PetStoreController {

    private AnimalService animalService;

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
}
