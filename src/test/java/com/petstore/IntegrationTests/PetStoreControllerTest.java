package com.petstore.IntegrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.petstore.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PetStoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AnimalRepository animalRepository;

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

    @Test
    public void retrieveListAnimalsFromShelterAndStore() throws Exception {
        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN"),
                new AnimalDTO( "3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW"),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE"),
                new AnimalDTO( "5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN")
        );

        List<Integer> animalsIds = List.of(1,2,3,4,5);
        MvcResult result = mockMvc
                .perform(post("/animals").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(animalsIds)))
                .andExpect(status().isCreated())
                .andReturn();

        List<AnimalDTO> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<AnimalDTO>>() {
        });

        assertEquals(animals, actual);
    }

    @Test
    public void createAdoptAnimalRequest() throws Exception {

        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN")
       );
        AdoptionRequestDTO adoptionRequest = new AdoptionRequestDTO("customer",animals);

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN")
       );
        animalsEntities = animalRepository.saveAll(animalsEntities);
        List<String> shelterNetIds = List.of(animalsEntities.get(0).getShelternateId(),animalsEntities.get(1).getShelternateId());

        CustomerRequest customerRequest = new CustomerRequest("customer", shelterNetIds);
        MvcResult result = mockMvc
                .perform(post("/adopt").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AdoptionRequestDTO actual = mapper.readValue(result.getResponse().getContentAsString(), AdoptionRequestDTO.class);
        assertEquals(adoptionRequest, actual);
    }

    @Test
    public void returnAnimalToShelter() throws Exception {

        Animal animal1= new Animal("101","Lion1","species",LocalDate.of(2015,12,27)
                        , "Male","Gold");
        Animal animal2=  new Animal("102","Monkey","species",LocalDate.of(2017,3,12)
                        , "Female","Gold");
        Animal animal3=     new Animal("103","Cat","species",LocalDate.of(2018,2,7)
                        , "Male","Gold");
        Animal animal4=    new Animal("104","Zebra","species",LocalDate.of(2020,1,1)
                        , "Female","Gold");

        Animal animal5=new Animal("101","Lion1","species",LocalDate.of(2015,10,27)
                , "Male","Gold");

        animalRepository.saveAll(List.of(animal1,animal2,animal3,animal4,animal5));

        mockMvc.perform(delete("/animalreturns")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(List.of(animal1.getShelternateId(),animal2.getShelternateId()))))
                .andExpect(status().isOk());

    }

    @Test
    public void returnSickAnimalToShelter() throws Exception {
        Animal animal1 = new Animal("101", "Lion1", "species", LocalDate.of(2015, 12, 27)
                , "Male", "Gold");
        Animal animal2 = new Animal("102", "Monkey", "species", LocalDate.of(2017, 3, 12)
                , "Female", "Gold");
        Animal animal3 = new Animal("103", "Cat", "species", LocalDate.of(2018, 2, 7)
                , "Male", "Gold");
        Animal animal4 = new Animal("104", "Zebra", "species", LocalDate.of(2020, 1, 1)
                , "Female", "Gold");

        Animal animal5 = new Animal("105", "Lion1", "species", LocalDate.of(2015, 10, 27)
                , "Male", "Gold");

        animalRepository.saveAll(List.of(animal1, animal2, animal3, animal4, animal5));

        mockMvc.perform(delete("/sickanimal/?shelternateId=101&diagnosis=fever"))
                .andExpect(status().isOk());
    }
}
