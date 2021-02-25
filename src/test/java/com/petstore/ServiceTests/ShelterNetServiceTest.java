package com.petstore.ServiceTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.ConvertListofIdsToArrayDTO;
import com.petstore.dto.FetchReturnAnimalsDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShelterNetServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ShelterNetService shelterNetService;

    ObjectMapper objectMapper;
    List<AnimalDTO> animalsDTO;
    ConvertListofIdsToArrayDTO convertListofIdsToArrayDTO ;


    @BeforeEach
    public void setUp(){
        objectMapper = new ObjectMapper();
        convertListofIdsToArrayDTO = new ConvertListofIdsToArrayDTO();
        animalsDTO =  List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",new ArrayList<>(),"Bob is super friendly"),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",new ArrayList<>(),"Seems to have fleas"),
                new AnimalDTO( "3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW",new ArrayList<>(),""),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE",new ArrayList<>(),""),
                new AnimalDTO( "5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN",new ArrayList<>(),"")
        );
    }

    @Test
    public void fetchAnimalsFromShelterNet() throws Exception {
        List<Integer> animalsIds = List.of(1,2,3,4,5);
        convertListofIdsToArrayDTO.setAnimalIds(List.of(1,2,3,4,5));
        List<FetchReturnAnimalsDTO> fetchReturnAnimalsDTOS =
                List.of(

                        new FetchReturnAnimalsDTO(1,"cat1","CAT", "2015-03-23","FEMALE","BLACK","Bob is super friendly"),
                        new FetchReturnAnimalsDTO( 2,"cat2","CAT","2016-03-23","MALE","BROWN","Seems to have fleas"),
                        new FetchReturnAnimalsDTO( 3,"dog1","DOG","2017-03-23","FEMALE","YELLOW",""),
                        new FetchReturnAnimalsDTO(4,"dog4","DOG","2015-03-23","MALE","WHITE",""),
                        new FetchReturnAnimalsDTO( 5,"bird","BIRD", "2015-03-23","FEMALE","GREEN","")
                );

        Mockito
                .when(restTemplate.postForEntity("https://shelternet-staging.herokuapp.com/animals/request/",
                        convertListofIdsToArrayDTO, String.class))
          .thenReturn(new ResponseEntity<String>(objectMapper.writeValueAsString(fetchReturnAnimalsDTOS),HttpStatus.OK));

        List<AnimalDTO> actual = shelterNetService.fetchAnimals(animalsIds);
        assertEquals(animalsDTO, actual);
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
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer",animalsDTO, Status.APPROVED.name()
                , "Approved, ready to be adopted");

        convertListofIdsToArrayDTO.setAnimalIds(List.of(1,2,3,4,5));
        when(restTemplate.postForEntity("https://shelternet-staging.herokuapp.com/animals/adopted",
                        convertListofIdsToArrayDTO, String.class))
          .thenReturn(new ResponseEntity<String>("OK",HttpStatus.OK));

        HttpStatus actual = shelterNetService.notifyAnimalAdoption(adoptionRequestDTO);

        assertEquals(HttpStatus.OK,actual);

    }
}
