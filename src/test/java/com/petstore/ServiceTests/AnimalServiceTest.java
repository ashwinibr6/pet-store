package com.petstore.ServiceTests;

import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.petstore.repository.AnimalRepository;
import com.petstore.service.AnimalService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//@WebMvcTest
@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {

    @Mock
    AnimalRepository animalRepository;

    @InjectMocks
    AnimalService animalService;

    @Test
    public void getAllAnimals(){


        Animal animal1=new Animal("101","Lion1","species", LocalDate.of(2015,12,27)
                , "Male","Gold");

        Animal animal2=new Animal("102","Lion1","species", LocalDate.of(2015,12,27)
                , "Male","Gold");

        List<Animal> expected=new ArrayList<>();
        expected.add(animal1);
        expected.add(animal2);

        when(animalRepository.findAll()).thenReturn(expected);

        List<AnimalDTO> actual=animalService.getAnimals();


        verify(animalRepository, times(1)).findAll();

        assertEquals(expected.get(0).getShelternateId(),actual.get(0).getShelternateId());
        assertEquals(expected.get(0).getAnimalName(),actual.get(0).getAnimalName());
        assertEquals(expected.get(0).getBirthDate(),actual.get(0).getBirthDate());
        assertEquals(expected.get(0).getColor(),actual.get(0).getColor());
        assertEquals(expected.get(0).getSex(),actual.get(0).getSex());
        assertEquals(expected.get(0).getSpecies(),actual.get(0).getSpecies());


        assertEquals(expected.size(),actual.size());


    }


    @Test
    public void addAnimals() throws Exception {
        List<AnimalDTO> animalsDto = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new AnimalDTO("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN"),
                new AnimalDTO("3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW"),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE"),
                new AnimalDTO("5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN")
        );

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN"),
                new Animal("3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW"),
                new Animal("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE"),
                new Animal("5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN")
        );
        when(animalRepository.saveAll(animalsEntities)).thenReturn(animalsEntities);


        List<AnimalDTO> actual = animalService.addAnimals(animalsDto);

        assertEquals(animalsDto, actual);
    }
}
