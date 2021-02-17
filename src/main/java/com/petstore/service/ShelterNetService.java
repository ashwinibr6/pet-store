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
                new AnimalDTO(1l,"1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",true,"fever"),
                new AnimalDTO(2l, "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",false,null),
                new AnimalDTO(3l, "3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW" ,false,null),
                new AnimalDTO(4l,"4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE" ,false,null),
                new AnimalDTO(5l, "5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN" ,true,"headache")
        );
        /* TO BE IMPLEMENTED */
      // String result = restTemplate.postForObject("http://localhost/add-comment", animalsIds, String.class);
       // List<AnimalDTO> actual = mapper.readValue(result, new TypeReference<List<AnimalDTO>>() {

        return animalsDto;
    }

    public HttpStatus returnAnimalToShelter(List<String> animalsIds) {
        //HttpStatus status=restTemplate.patchForObject("https://shelternet.herokuapp.com/", animalsIds, HttpStatus.class);
        //return status;
        return HttpStatus.OK;
    }

    public HttpStatus returnSickAnimalToShelter(List<String> shelterIds) {
        return HttpStatus.OK;
    }
}
