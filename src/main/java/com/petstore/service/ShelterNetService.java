package com.petstore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShelterNetService {

    private RestTemplate restTemplate;
    ConvertListofIdsToArrayDTO convertListofIdsToArrayDTO = new ConvertListofIdsToArrayDTO();
    private ObjectMapper mapper = new ObjectMapper();

    public ShelterNetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AnimalDTO> fetchAnimals(List<Integer> animalsIds) throws Exception {
        convertListofIdsToArrayDTO.setAnimalIds(animalsIds);
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("user", "stagingPass1"));
        String result = restTemplate.postForObject("http://shelternet-staging.herokuapp.com/animals/request/", convertListofIdsToArrayDTO, String.class);
        List<FetchReturnAnimalsDTO> fetchReturnAnimalsDTOS = mapper.readValue(result, new TypeReference<List<FetchReturnAnimalsDTO>>() {});

        List<AnimalDTO> animalsDto = fetchReturnAnimalsDTOS.stream().map(fetchReturnAnimalsDTO ->
                new AnimalDTO(String.valueOf(fetchReturnAnimalsDTO.getId()), fetchReturnAnimalsDTO.getName(), fetchReturnAnimalsDTO.getSpecies(), LocalDate.parse(fetchReturnAnimalsDTO.getBirthDate()),
                        fetchReturnAnimalsDTO.getSex(), fetchReturnAnimalsDTO.getColor(), null, fetchReturnAnimalsDTO.getNotes())).collect(Collectors.toList());
        return animalsDto;
    }

    public HttpStatus returnAnimalToShelter(List<String> animalsIds) {
        List<AnimalReturnDto> animals=animalsIds.stream().map(id->new AnimalReturnDto(id,"")).collect(Collectors.toList());
        //HttpStatus status=restTemplate.postForObject("https://shelternet.herokuapp.com/animals/return", animals, HttpStatus.class);
        //return status;
        return HttpStatus.OK;
    }

    public HttpStatus returnSickAnimalToShelter(String shelternateId,String diagnosis) {
        List<AnimalReturnDto> animals=Arrays.asList(new AnimalReturnDto(shelternateId,diagnosis));
//        HttpStatus status=restTemplate.patchForObject("https://shelternet.herokuapp.com/animals/return",animals,HttpStatus.class);
        //return status;
        return HttpStatus.OK;
    }

    public HttpStatus notifyAnimalAdoption(AdoptionRequestDTO adoptionRequestDTO) {
        List<Integer> shelterNetIds = adoptionRequestDTO.getAnimalDTOS()
                .stream().map(animaldto -> Integer.parseInt(animaldto.getShelternateId())).collect(Collectors.toList());

        convertListofIdsToArrayDTO.setAnimalIds(shelterNetIds);
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("user", "stagingPass1"));

        ResponseEntity<String> status=restTemplate.postForEntity(
                "https://shelternet-staging.herokuapp.com/animals/adopted",
                convertListofIdsToArrayDTO, String.class);
        return status.getStatusCode();

    }
}
