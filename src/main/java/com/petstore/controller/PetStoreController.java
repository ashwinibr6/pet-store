package com.petstore.controller;

import com.petstore.dto.AnimalDTO;
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



    @PostMapping("/animals")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AnimalDTO> addAnimals(@RequestBody List<Integer> animalIds){

        List<AnimalDTO> animals = shelterNetService.fetchAnimals(animalIds);
        return animalService.addAnimals(animals);
    }

    @DeleteMapping("/animalreturns")
    @ResponseStatus(HttpStatus.OK)
    public void returnAnimalToShelter(@RequestBody List<String> shelterIds){

        HttpStatus status=shelterNetService.returnAnimalToShelter(shelterIds);
        if(status.is2xxSuccessful()){
            animalService.removeAnimals(shelterIds);
        }
    }

    @DeleteMapping("/sickanimal")
    @ResponseStatus(HttpStatus.OK)
    public void  returnSickAnimalToShelter(@RequestParam String shelternateId, @RequestParam String diagnosis){
        if(animalService.getAnimal(shelternateId)!=null){
            HttpStatus status=shelterNetService.returnSickAnimalToShelter(shelternateId,diagnosis);
            if(status.is2xxSuccessful()){
                animalService.removeAnimals(List.of(shelternateId));
            }
        }

    }





}
