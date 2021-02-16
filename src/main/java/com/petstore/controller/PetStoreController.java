package com.petstore.controller;

import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.petstore.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
public class PetStoreController {

    @Autowired
    private AnimalService animalService;

    //public PetStoreController(AnimalService animalService) {
//        this.animalService = animalService;
//    }
//    @Autowired
//    private RestTemplate restTemplate;

    @GetMapping("home")
    public String homePage(){
        return "Welcome to Pet Store";
    }

//    @PostMapping("/animal")
//    @ResponseStatus(HttpStatus.CREATED)
//    public AnimalDTO addAnimal(@RequestBody AnimalDTO animalDTO){
//        return animalService.addAnimal(animalDTO);
//    }

    @GetMapping("animals")
    public List<AnimalDTO> getAllAnimals(){
        return animalService.getAnimals();
    }



    @PostMapping("/animals")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AnimalDTO> addAnimals(@RequestBody List<Integer> animalIds){
        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new AnimalDTO("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN"),
                new AnimalDTO("3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW"),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE"),
                new AnimalDTO("5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN")
        );

//        String result = "";
//        try {
//             result = restTemplate.postForObject("http://localhost/add-comment", animalIds, String.class);
//            System.out.println("addComment: " + result);
//        } catch (HttpClientErrorException e) {
//            result = e.getMessage();
//        }

        return animalService.addAnimals(animals);
    }
}
