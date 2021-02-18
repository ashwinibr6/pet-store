package com.petstore.service;

import com.petstore.dto.AnimalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShelterNetService {

    private RestTemplate restTemplate;

    public ShelterNetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<AnimalDTO> fetchAnimals(List<Integer> animalsIds) {
        List<AnimalDTO> animalsDto = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK"),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN"),
                new AnimalDTO( "3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW"),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE"),
                new AnimalDTO( "5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN")
        );
        /* TO BE IMPLEMENTED */
      // String result = restTemplate.postForObject("http://localhost/add-comment", animalsIds, String.class);
       // List<AnimalDTO> actual = mapper.readValue(result, new TypeReference<List<AnimalDTO>>() {

        return animalsDto;
    }

    public HttpStatus returnAnimalToShelter(List<String> animalsIds) {
        //HttpStatus status=restTemplate.patchForObject("https://shelternet.herokuapp.com/?shelterId101=&diagnis=fever", animalsIds, HttpStatus.class);
        //return status;
        return HttpStatus.OK;
    }

    public HttpStatus returnSickAnimalToShelter(String shelternateId,String diagnosis) {

//        HttpStatus status=restTemplate.patchForObject("https://shelternet.herokuapp.com/?shelternateId="+shelternateId,diagnosis,HttpStatus.class);
        //return status;
        return HttpStatus.OK;
    }
}
