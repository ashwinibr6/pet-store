package com.petstore.ServiceTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.model.Status;
import com.petstore.service.ShelterNetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ShelterNetServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ShelterNetService shelterNetService;

    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    public void fetchAnimalsFromShelterNet() throws JsonProcessingException {
        List<Integer> animalsIds = List.of(1,2,3,4,5);
        List<AnimalDTO> animalsDto = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN"),
                new AnimalDTO( "3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW"),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE"),
                new AnimalDTO( "5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN")
        );
        /* To be uncommented once we get shelter end point*/
//        Mockito
//                .when(restTemplate.postForObject("http://localhost/add-comment", animalsIds, String.class))
//          .thenReturn(objectMapper.writeValueAsString(animalsDto));

        List<AnimalDTO> actual = shelterNetService.fetchAnimals(animalsIds);
        assertEquals(animalsDto, actual);
    }

    @Test
    public void retunAnimalToShelter(){
        List<String> animalsIds = List.of("1","2","3","4","5");

        /* To be uncommented once we get shelter end point*/
        //when(restTemplate.patchForObject("/returnanimals", animalsIds, HttpStatus.class)).thenReturn(HttpStatus.OK);

        HttpStatus actual = shelterNetService.returnAnimalToShelter(animalsIds);
        assertEquals(HttpStatus.OK,actual);
    }
    @Test
    public void returnSickAnimalToShelter(){
//        when(restTemplate.patchForObject("https://shelternet.herokuapp.com/?shelternateId=101","fever",HttpStatus.class)).thenReturn(HttpStatus.OK);
        HttpStatus actual=shelterNetService.returnSickAnimalToShelter("101","fever");
        assertEquals(HttpStatus.OK,actual);
    }

    @Test
    public void notifyAnimalAdoption() throws JsonProcessingException {
        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT",
                        LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new AnimalDTO("2","cat2","CAT",
                        LocalDate.of(2016,03,23),"MALE","BROWN")
        );
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer",animals, Status.APPROVED.name()
                , "Approved, ready to be adopted");


        /* To be uncommented once we get shelter end point*/
//        Mockito
//                .when(restTemplate.postForObject("http://localhost/add-comment", adoptionRequestDTO, HttpStatus.class))
//          .thenReturn(HttpStatus.OK);


        HttpStatus actual = shelterNetService.notifyAnimalAdoption(adoptionRequestDTO);
        assertEquals(HttpStatus.OK,actual);

    }
}
