package com.petstore.RestDocs;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.model.Animal;
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

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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

    @BeforeEach
    public void setUp(){
        mapper = new ObjectMapper();
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
                        fieldWithPath("[0].bond").description("Bond of the animal")
                )));
    }

    @Test
    public void createAdoptAnimalRequest() throws Exception {

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>()),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
        );
        animalsEntities = animalRepository.saveAll(animalsEntities);
        List<String> shelterNetIds = List.of(animalsEntities.get(0).getShelternateId(),animalsEntities.get(1).getShelternateId());


        CustomerRequest customerRequest = new CustomerRequest("customer", shelterNetIds);
        mockMvc
                .perform(post("/adopt").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andDo(document("createAdoptAnimalRequest", responseFields(
                        fieldWithPath("client").description("Name of the client"),
                        fieldWithPath("animalDTOS.[0].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("animalDTOS.[0].animalName").description("Name of the animal"),
                        fieldWithPath("animalDTOS.[0].species").description("Species of the animal"),
                        fieldWithPath("animalDTOS.[0].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("animalDTOS.[0].sex").description("Sex of the animal"),
                        fieldWithPath("animalDTOS.[0].color").description("Color of the animal"),
                        fieldWithPath("animalDTOS.[0].bond").description("Bond of the animal")

                )));
    }

    @Test
    public void getAllAnimals() throws Exception {

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>()),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
        );
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
                        fieldWithPath("[].bond").description("Bond of the animal")
                )));
    }

    @Test
    public void returnAnimalToShelter() throws Exception {
        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>()),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
        );
        animalRepository.saveAll(animalsEntities);
        List<String> animalsIds = List.of("1","2");

        mockMvc
                .perform(delete("/animalreturns").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds))).andExpect(status().isOk())
                .andDo(document("returnAnimalToShelter"));
    }

    @Test
    public void returnSickAnimalToShelter() throws Exception {

        Animal animal1=new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>());
        Animal animal2=new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>());

        List<String> animalsIds = List.of("1","2");
        animalRepository.saveAll(List.of(animal1,animal2));

        mockMvc
                .perform(delete("/sickanimal/?shelternateId=1&diagnosis=fever"))
                        .andExpect(status().isOk())
                .andDo(document("returnSickAnimalToShelter"));
    }
    @Test
    public void bondAnimal() throws Exception {

        Animal animal1 = new Animal("1", "Lion1", "species", LocalDate.of(2015, 12, 27)
                , "Male", "Gold", new ArrayList<>());
        Animal animal2 = new Animal("2", "Monkey", "species", LocalDate.of(2017, 3, 12)
                , "Female", "Gold", new ArrayList<>());
        Animal animal3 = new Animal("3", "Cat", "species", LocalDate.of(2018, 2, 7)
                , "Male", "Gold", new ArrayList<>());
        Animal animal4 = new Animal("4", "Zebra", "species", LocalDate.of(2020, 1, 1)
                , "Female", "Gold", new ArrayList<>());

        Animal animal5 = new Animal("5", "Lion1", "species", LocalDate.of(2015, 10, 27)
                , "Male", "Gold", new ArrayList<>());

        animalRepository.saveAll(List.of(animal1, animal2, animal3, animal4, animal5));

        mockMvc.perform(patch("/bondedanimal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(1,2,3))))
                .andExpect(status().isOk()).andDo(document("bondedanimal"));
    }
    @Test
    public void getAnimal() throws Exception {
        Animal animal1 = new Animal("1", "Lion1", "species", LocalDate.of(2015, 12, 27)
                , "Male", "Gold", new ArrayList<>());
        animalRepository.save(animal1);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/animal/{shelternateID}",animal1.getShelternateId())).andExpect(status().isOk()).andDo(document("getAnimal",pathParameters(
                parameterWithName("shelternateID").description("The shelter id of the animal")),responseFields(
                fieldWithPath("shelternateId").description("shelternateId of the animal"),
                fieldWithPath("animalName").description("Name of the animal"),
                fieldWithPath("species").description("Species of the animal"),
                fieldWithPath("birthDate").description("BirthDate of the animal"),
                fieldWithPath("sex").description("Sex of the animal"),
                fieldWithPath("color").description("Color of the animal"),
                fieldWithPath("bond.[]").description("Bond of the animal"))));
    }
}
