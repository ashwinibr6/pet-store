package com.petstore.IntegrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.dto.AnimalDTO;
import com.petstore.model.Animal;
import com.petstore.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
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

//
//    @Autowired
//    private RestTemplate restTemplate;

//    @Autowired
//    private MockRestServiceServer mockServer;

    @BeforeEach
    public  void setup(){

       // mockServer = MockRestServiceServer.createServer(restTemplate);
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
                new AnimalDTO("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN"),
                new AnimalDTO("3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW"),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE"),
                new AnimalDTO("5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN")
        );

//
//        mockServer.expect(requestTo("http://shelternet.net/animal-request"))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess(mapper.writeValueAsString(animals), MediaType.APPLICATION_JSON));
//

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
}
