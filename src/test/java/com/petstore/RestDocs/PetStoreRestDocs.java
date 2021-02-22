package com.petstore.RestDocs;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AnimalReturnDto;
import com.petstore.model.AdoptionRequest;
import com.petstore.model.Animal;
import com.petstore.model.Status;
import com.petstore.repository.AdoptionRequestRepository;
import com.petstore.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.swing.text.Document;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/target/snippets")
@Transactional
public class PetStoreRestDocs {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper;

    @Autowired
    AnimalRepository animalRepository;

    @Autowired
    AdoptionRequestRepository adoptionRequestRepository;
    List<Animal> animalsEntities;

    @BeforeEach
    public void setUp(){
        mapper = new ObjectMapper();

       animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>(),""),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>(),"")
        );
    }

    @Test
    void homePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andDo(document("home"));
    }

    @Test
    public void retrieveListAnimalsFromShelterAndStore() throws Exception {

        List<Integer> animalsIds = List.of(1,2,3,4,5);
        mockMvc
                .perform(post("/animals").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds)))
                .andExpect(status().isCreated())
                .andDo(document("fetchAnimals", responseFields(
                        fieldWithPath("[0].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("[0].animalName").description("Name of the animal"),
                        fieldWithPath("[0].species").description("Species of the animal"),
                        fieldWithPath("[0].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("[0].sex").description("Sex of the animal"),
                        fieldWithPath("[0].color").description("Color of the animal"),
                        fieldWithPath("[0].bond").description("Bond of the animal"),
                        fieldWithPath("[0].note").description("Animal's note")
                )));
    }

    @Test
    public void createAdoptAnimalRequest() throws Exception {


        animalsEntities = animalRepository.saveAll(animalsEntities);
        List<String> shelterNetIds = List.of(animalsEntities.get(0).getShelternateId(),animalsEntities.get(1).getShelternateId());


        CustomerRequest customerRequest = new CustomerRequest("customer", shelterNetIds);
        mockMvc
                .perform(post("/adopt").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andDo(document("createAdoptAnimalRequest", responseFields(
                        fieldWithPath("client").description("Name of the client"),
                        fieldWithPath("comment").description("The comment of the request approval"),
                        fieldWithPath("status").description("The status of the request"),
                        fieldWithPath("animalDTOS.[0].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("animalDTOS.[0].animalName").description("Name of the animal"),
                        fieldWithPath("animalDTOS.[0].species").description("Species of the animal"),
                        fieldWithPath("animalDTOS.[0].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("animalDTOS.[0].sex").description("Sex of the animal"),
                        fieldWithPath("animalDTOS.[0].color").description("Color of the animal"),
                        fieldWithPath("animalDTOS.[0].bond").description("Bond of the animal"),
                        fieldWithPath("animalDTOS.[0].note").description("Animal's note")

                )));
    }

    @Test
    public void getAllAnimals() throws Exception {


        animalRepository.saveAll(animalsEntities);

        mockMvc
                .perform(get("/animals").contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].shelternateId").value("1"))
                .andExpect(jsonPath("$.[0].species").value("CAT"))
                .andExpect(jsonPath("$.[0].color").value("BLACK"))
                .andDo(document("getAllAnimals", responseFields(
                        fieldWithPath("[].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("[].animalName").description("Name of the animal"),
                        fieldWithPath("[].species").description("Species of the animal"),
                        fieldWithPath("[].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("[].sex").description("Sex of the animal"),
                        fieldWithPath("[].color").description("Color of the animal"),
                        fieldWithPath("[].bond").description("Bond of the animal"),
                        fieldWithPath("[].note").description("Animal's note")
                )));
    }

    @Test
    public void returnAnimalToShelter() throws Exception {

        animalRepository.saveAll(animalsEntities);
        List<String> animalsIds = List.of("1","2");

        mockMvc
                .perform(delete("/animalreturns").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds))).andExpect(status().isOk())
                .andDo(document("returnAnimalToShelter"));
    }

    @Test
    public void returnSickAnimalToShelter() throws Exception {


        animalRepository.saveAll(animalsEntities);

        mockMvc
                .perform(delete("/sickanimal/?shelternateId=1&diagnosis=fever"))
                        .andExpect(status().isOk())
                .andDo(document("returnSickAnimalToShelter"));
    }
    @Test
    public void bondAnimal() throws Exception {

        animalRepository.saveAll(animalsEntities);

        mockMvc.perform(patch("/bondedanimal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(1,2))))
                .andExpect(status().isOk()).andDo(document("bondedanimal"));
    }
    @Test
    public void getAnimal() throws Exception {

        animalRepository.save(animalsEntities.get(0));
        mockMvc.perform(RestDocumentationRequestBuilders.get("/animal/{shelternateID}",animalsEntities.get(0).getShelternateId())).andExpect(status().isOk()).andDo(document("getAnimal",pathParameters(
                parameterWithName("shelternateID").description("The shelter id of the animal")),responseFields(
                fieldWithPath("shelternateId").description("shelternateId of the animal"),
                fieldWithPath("animalName").description("Name of the animal"),
                fieldWithPath("species").description("Species of the animal"),
                fieldWithPath("birthDate").description("BirthDate of the animal"),
                fieldWithPath("sex").description("Sex of the animal"),
                fieldWithPath("color").description("Color of the animal"),
                fieldWithPath("bond.[]").description("Bond of the animal"),
                fieldWithPath("note").description("Animal's note"))));
    }

    @Test
    public void approveAdoptionRequest() throws Exception {


        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");

        mockMvc
                .perform(RestDocumentationRequestBuilders.put("/adopt/request/{id}",adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andDo(document("ApproveAdoptionRequest",pathParameters(
                        parameterWithName("id").description("The adoption request ID")),responseFields(
                        fieldWithPath("adoptionRequestDTO.comment").description("The comment of the request approval"),
                        fieldWithPath("adoptionRequestDTO.status").description("The status of the request"),
                        fieldWithPath("adoptionRequestDTO.client").description("The client details"),

                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].shelternateId").description("shelternateId of the animal"),
                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].animalName").description("Name of the animal"),
                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].species").description("Species of the animal"),
                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].birthDate").description("BirthDate of the animal"),
                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].sex").description("Sex of the animal"),
                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].color").description("Color of the animal"),
                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].bond").description("Bond of the animal"),
                fieldWithPath("adoptionRequestDTO.animalDTOS.[0].note").description("Animal's note"),
                fieldWithPath("shelterNetNotificationStatus").description("ShelterNet Notification Status"))));
    }

    @Test
    public void denyAdoptionRequestInSeparable() throws Exception {

        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(RestDocumentationRequestBuilders.put("/adopt/request/{id}",adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andDo(document("DenyInSeparableAdoptionRequest",pathParameters(
                        parameterWithName("id").description("The adoption request ID")),responseFields(
                        fieldWithPath("adoptionRequestDTO.comment").description("The comment of the request approval"),
                        fieldWithPath("adoptionRequestDTO.status").description("The status of the request"),
                        fieldWithPath("adoptionRequestDTO.client").description("The client details"),

                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].animalName").description("Name of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].species").description("Species of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].sex").description("Sex of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].color").description("Color of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].bond").description("Bond of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].note").description("Animal's note"),
                        fieldWithPath("shelterNetNotificationStatus").description("ShelterNet Notification Status"))));


    }


    @Test
    public void denyAdoptionRequestNonSeparable() throws Exception {

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2"),""),
                new Animal("3","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>(),"")
        );
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(RestDocumentationRequestBuilders.put("/adopt/request/{id}",adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andDo(document("DenyNonSeparableAdoptionRequest",pathParameters(
                        parameterWithName("id").description("The adoption request ID")),responseFields(
                        fieldWithPath("adoptionRequestDTO.comment").description("The comment of the request approval"),
                        fieldWithPath("adoptionRequestDTO.status").description("The status of the request"),
                        fieldWithPath("adoptionRequestDTO.client").description("The client details"),

                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].animalName").description("Name of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].species").description("Species of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].sex").description("Sex of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].color").description("Color of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].bond").description("Bond of the animal"),
                        fieldWithPath("adoptionRequestDTO.animalDTOS.[0].note").description("Animal's note"),
                        fieldWithPath("shelterNetNotificationStatus").description("ShelterNet Notification Status"))));


    }

    @Test
    public void returnRequestedAnimalToShelter(){
        animalRepository.saveAll(animalsEntities);
        List<AnimalReturnDto> expected = List.of(new AnimalReturnDto("1", "Bob is super friendly"), new AnimalReturnDto("2", "Seems to have fleas"));

        mockMvc.perform(delete("/animals/return-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(animalsEntities.get(0).getShelternateId(), animalsEntities.get(1).getShelternateId()))))
                .andExpect(status().isOk())
                .andDo(document("returnRequestedAnimalToShelter",responseFields(
                        fieldWithPath("[].id").description("ShelterNetId of Animal"),
                        fieldWithPath("[].note]").description("Animal note"))));

    }
}
