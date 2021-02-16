package com.petstore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class PetStoreController {

    @GetMapping("home")
    public String homePage(){
        return "Welcome to Pet Store";
    }
}
