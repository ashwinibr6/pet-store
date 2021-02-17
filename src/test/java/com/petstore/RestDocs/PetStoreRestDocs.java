package com.petstore.RestDocs;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.dto.AnimalDTO;
import com.petstore.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.List;


import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/target/snippets")
public class PetStoreRestDocs {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper;

    @Autowired
    AnimalRepository animalRepository;


    @BeforeEach
    public void setUp(){
        mapper = new ObjectMapper();
        animalRepository.deleteAll();

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
                        fieldWithPath("[0].id").description("Id of the animal in pet store"),
                        fieldWithPath("[0].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("[0].animalName").description("Name of the animal"),
                        fieldWithPath("[0].species").description("Species of the animal"),
                        fieldWithPath("[0].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("[0].sex").description("Sex of the animal"),
                        fieldWithPath("[0].color").description("Color of the animal")

                )));


    }

    @Test
    public void getAllAnimals() throws Exception {

        AnimalDTO animalDTO1=new AnimalDTO(1l,"1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK");
        AnimalDTO animalDTo2=new AnimalDTO(2l, "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN");


        mockMvc
                .perform(post("/animal").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalDTO1))).andExpect(status().isCreated());

        mockMvc
                .perform(post("/animal").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalDTo2))).andExpect(status().isCreated());

        mockMvc
                .perform(get("/animals").contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].shelternateId").value("1"))
                .andExpect(jsonPath("$.[0].species").value("CAT"))
                .andExpect(jsonPath("$.[0].color").value("BLACK"))
                .andDo(document("getAllAnimals", responseFields(
                        fieldWithPath("[].id").description("Id of the animal in pet store"),
                        fieldWithPath("[].shelternateId").description("shelternateId of the animal"),
                        fieldWithPath("[].animalName").description("Name of the animal"),
                        fieldWithPath("[].species").description("Species of the animal"),
                        fieldWithPath("[].birthDate").description("BirthDate of the animal"),
                        fieldWithPath("[].sex").description("Sex of the animal"),
                        fieldWithPath("[].color").description("Color of the animal")

                )));

    }

    @Test
    public void returnAnimalToShelter() throws Exception {

        AnimalDTO animalDTO1=new AnimalDTO(1l,"1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK");
        AnimalDTO animalDTo2=new AnimalDTO(2l, "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN");

        List<String> animalsIds = List.of("1","2");

        mockMvc
                .perform(post("/animal").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalDTO1))).andExpect(status().isCreated());

        mockMvc
                .perform(post("/animal").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalDTo2))).andExpect(status().isCreated());


        mockMvc
                .perform(delete("/animalreturns").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds))).andExpect(status().isOk())
                .andDo(document("returnAnimalToShelter"));
    }



}
