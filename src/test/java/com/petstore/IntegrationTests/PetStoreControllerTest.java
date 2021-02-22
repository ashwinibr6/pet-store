package com.petstore.IntegrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.model.*;
import com.petstore.repository.AdoptionRequestRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PetStoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AnimalRepository animalRepository;

    @Autowired
    AdoptionRequestRepository adoptionRequestRepository;

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
                , "Male","Gold", new ArrayList<>()),
                new Animal("101","Lion2","species",LocalDate.of(2017,3,12)
                        , "Female","Gold", new ArrayList<>())));

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk());
    }

    @Test
    public void retrieveListAnimalsFromShelterAndStore() throws Exception {
        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",new ArrayList<>()),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",new ArrayList<>()),
                new AnimalDTO( "3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW",new ArrayList<>()),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE",new ArrayList<>()),
                new AnimalDTO( "5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN",new ArrayList<>())
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
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",new ArrayList<>()),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",new ArrayList<>())
       );
        AdoptionRequestDTO adoptionRequest = new AdoptionRequestDTO("customer",animals, Status.PENDING.toString(),"");

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>()),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
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
                        , "Male","Gold", new ArrayList<>());
        Animal animal2=  new Animal("102","Monkey","species",LocalDate.of(2017,3,12)
                        , "Female","Gold", new ArrayList<>());
        Animal animal3=     new Animal("103","Cat","species",LocalDate.of(2018,2,7)
                        , "Male","Gold", new ArrayList<>());
        Animal animal4=    new Animal("104","Zebra","species",LocalDate.of(2020,1,1)
                        , "Female","Gold", new ArrayList<>());

        Animal animal5=new Animal("101","Lion1","species",LocalDate.of(2015,10,27)
                , "Male","Gold", new ArrayList<>());

        animalRepository.saveAll(List.of(animal1,animal2,animal3,animal4,animal5));

        mockMvc.perform(delete("/animalreturns")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(List.of(animal1.getShelternateId(),animal2.getShelternateId()))))
                .andExpect(status().isOk());

    }

    @Test
    public void returnSickAnimalToShelter() throws Exception {
        Animal animal1 = new Animal("101", "Lion1", "species", LocalDate.of(2015, 12, 27)
                , "Male", "Gold", new ArrayList<>());
        Animal animal2 = new Animal("102", "Monkey", "species", LocalDate.of(2017, 3, 12)
                , "Female", "Gold", new ArrayList<>());
        Animal animal3 = new Animal("103", "Cat", "species", LocalDate.of(2018, 2, 7)
                , "Male", "Gold", new ArrayList<>());
        Animal animal4 = new Animal("104", "Zebra", "species", LocalDate.of(2020, 1, 1)
                , "Female", "Gold", new ArrayList<>());

        Animal animal5 = new Animal("105", "Lion1", "species", LocalDate.of(2015, 10, 27)
                , "Male", "Gold", new ArrayList<>());

        animalRepository.saveAll(List.of(animal1, animal2, animal3, animal4, animal5));

        mockMvc.perform(delete("/sickanimal/?shelternateId=101&diagnosis=fever"))
                .andExpect(status().isOk());
    }

    @Test
    public void approveAdoptionRequest() throws Exception {

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",
                        List.of("1")),
                new Animal("3","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
        );
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");

        mockMvc
                .perform(put("/adopt/request/"+adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("adoptionRequestDTO.comment").value("Approved, ready to be adopted"))
                .andExpect(jsonPath("adoptionRequestDTO.status").value("APPROVED"))
                .andExpect(jsonPath("adoptionRequestDTO.client").value("customer"))
                .andExpect(jsonPath("shelterNetNotificationStatus").value("OK"));


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
                .andExpect(status().isOk());

        List<String> expected=new ArrayList<>();
        expected.add("2");
        expected.add("3");

        mockMvc.perform(get("/animal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shelternateId").value("1"))
                .andExpect(jsonPath("$.animalName").value("Lion1"))
                .andExpect(jsonPath("$.species").value("species"))
                .andExpect(jsonPath("$.birthDate").value("2015-12-27"))
                .andExpect(jsonPath("$.sex").value("Male"))
                .andExpect(jsonPath("$.color").value("Gold"))
                .andExpect(jsonPath("$.bond").value(expected));



    }

    @Test
    public void denyAdoptionRequestInSeparable() throws Exception {

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",
                        List.of("1")),
                new Animal("3","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
        );
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(put("/adopt/request/"+adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("adoptionRequestDTO.comment").value("Denied, Can't be adopted"))
                .andExpect(jsonPath("adoptionRequestDTO.status").value("DENIED"))
                .andExpect(jsonPath("adoptionRequestDTO.client").value("customer"))
                .andExpect(jsonPath("shelterNetNotificationStatus").doesNotExist());


    }


    @Test
    public void denyAdoptionRequestNonSeparable() throws Exception {

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new Animal("3","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
        );
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(put("/adopt/request/"+adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("adoptionRequestDTO.comment").value("Denied, Can't be adopted"))
                .andExpect(jsonPath("adoptionRequestDTO.status").value("DENIED"))
                .andExpect(jsonPath("adoptionRequestDTO.client").value("customer"))
                .andExpect(jsonPath("shelterNetNotificationStatus").doesNotExist());


    }

    @Test
    public void carryItemToStoreCatalog() throws Exception {
       StoreItem storeItem=new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood","Food for cats",9.99);
       mockMvc.perform(post("/storeCatalog/carry").contentType(MediaType.APPLICATION_JSON)
       .content(mapper.writeValueAsString(storeItem)))
               .andExpect(status().isAccepted())
               .andExpect(jsonPath("sku").value(1))
               .andExpect(jsonPath("itemCategory").value("FOOD"))
               .andExpect(jsonPath("animalType").value("CAT"))
               .andExpect(jsonPath("brand").value("Brand"))
               .andExpect(jsonPath("name").value("SomeFood"))
               .andExpect(jsonPath("description").value("Food for cats"))
               .andExpect(jsonPath("price").value("9.99"));






    }
}
