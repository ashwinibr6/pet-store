package com.petstore.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.dto.AnimalDTO;
import com.petstore.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.petstore.model.Animal;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PetStoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AnimalRepository animalRepository;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper =  new ObjectMapper();
    }

    @Test
    public void homePage() throws Exception {
        String response = mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Welcome to Pet Store", response);
    }

    @Test
    public void getAllAnimals() throws Exception {
        animalRepository.saveAll(List.of(
                new Animal("101","Lion1","species",LocalDate.of(2015,12,27)
                , "Male","Gold"),
                new Animal("101","Lion2","species",LocalDate.of(2017,3,12)
                        , "Female","Gold")));

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk());
    }


    //Given there are animals hosted in Pet Store
    //When I return them back to Shelternet
    //Then I no longer see them in my store

    @Test
    public void returnAnimalToShelter() throws Exception {
        animalRepository.saveAll(List.of(
                new Animal("101","Lion1","species",LocalDate.of(2015,12,27)
                        , "Male","Gold"),
                new Animal("102","Monkey","species",LocalDate.of(2017,3,12)
                        , "Female","Gold"),
                new Animal("103","Cat","species",LocalDate.of(2018,2,7)
                        , "Male","Gold"),
                new Animal("104","Zebra","species",LocalDate.of(2020,1,1)
                        , "Female","Gold")));

        Animal animal=new Animal("101","Lion1","species",LocalDate.of(2015,10,27)
                , "Male","Gold");

        List<String> animalsShelterIds=new ArrayList<>();
        animalsShelterIds.add(animal.getShelternateId());


        mockMvc.perform(delete("/animalreturns")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(animalsShelterIds)))
                .andExpect(status().isOk());

    }



}
