package com.petstore.IntegrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ItemPurchaseRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.AnimalReturnDto;
import com.petstore.model.AdoptionRequest;
import com.petstore.model.Animal;
import com.petstore.model.Status;
import com.petstore.model.*;
import com.petstore.repository.AdoptionRequestRepository;
import com.petstore.repository.AnimalRepository;
import com.petstore.repository.StoreItemRepository;
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

    @Autowired
    StoreItemRepository storeItemRepository;

    ObjectMapper mapper;
    List<AnimalDTO> animalsDTO;
    List<Animal> animalsEntities;
    List<StoreItem> storeItems;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();

        animalsDTO = List.of(
                new AnimalDTO("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK", List.of("2", "3"), "Bob is super friendly"),
                new AnimalDTO("2", "cat2", "CAT", LocalDate.of(2016, 03, 23), "MALE", "BROWN", List.of("1", "3"), "Seems to have fleas"),
                new AnimalDTO("3", "dog1", "DOG", LocalDate.of(2017, 03, 23), "FEMALE", "YELLOW", List.of("1", "2"), ""),
                new AnimalDTO("4", "dog4", "DOG", LocalDate.of(2015, 03, 23), "MALE", "WHITE", new ArrayList<>(), ""),
                new AnimalDTO("5", "bird", "BIRD", LocalDate.of(2015, 03, 23), "FEMALE", "GREEN", new ArrayList<>(), "")
        );

        animalsEntities = List.of(
                new Animal("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK", List.of("2", "3"), "Bob is super friendly"),
                new Animal("2", "cat2", "CAT", LocalDate.of(2016, 03, 23), "MALE", "BROWN", List.of("1", "3"), "Seems to have fleas"),
                new Animal("3", "dog1", "DOG", LocalDate.of(2017, 03, 23), "FEMALE", "YELLOW", List.of("1", "2"), ""),
                new Animal("4", "dog4", "DOG", LocalDate.of(2015, 03, 23), "MALE", "WHITE", new ArrayList<>(), ""),
                new Animal("5", "bird", "BIRD", LocalDate.of(2015, 03, 23), "FEMALE", "GREEN", new ArrayList<>(), ""));

         storeItems = List.of(new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood","Food for cats",9.99, 10),
                 new StoreItem(2L, ItemCategory.TOYS.name(),AnimalType.DOG.name(),"Brand","Toy","Toy for dog",4.99, 15),
                 new StoreItem(3L, ItemCategory.HOMES.name(),AnimalType.DOG.name(),"Brand","Home","Home for dog",20.99, 30));

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
        animalRepository.saveAll(animalsEntities);

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk());
    }

    @Test
    public void retrieveListAnimalsFromShelterAndStore() throws Exception {
        List<Integer> animalsIds = List.of(1, 2, 3, 4, 5);
        MvcResult result = mockMvc
                .perform(post("/animals").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds)))
                .andExpect(status().isCreated())
                .andReturn();

        List<AnimalDTO> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<AnimalDTO>>() {
        });

        assertEquals(animalsDTO, actual);
    }

    @Test
    public void createAdoptAnimalRequest() throws Exception {
        AdoptionRequestDTO adoptionRequest = new AdoptionRequestDTO("customer", List.of(animalsDTO.get(0), animalsDTO.get(1)), Status.PENDING.toString(), "");
        animalsEntities = animalRepository.saveAll(animalsEntities);
        List<String> shelterNetIds = List.of(animalsEntities.get(0).getShelternateId(), animalsEntities.get(1).getShelternateId());

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
        animalRepository.saveAll(animalsEntities);

        mockMvc.perform(delete("/animalreturns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(animalsEntities.get(0).getShelternateId(), animalsEntities.get(1).getShelternateId()))))
                .andExpect(status().isOk());

    }

    @Test
    public void returnSickAnimalToShelter() throws Exception {
        animalRepository.saveAll(animalsEntities);

        mockMvc.perform(delete("/sickanimal/?shelternateId=1&diagnosis=fever"))
                .andExpect(status().isOk());
    }

    @Test
    public void approveAdoptionRequest() throws Exception {
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");

        mockMvc
                .perform(put("/adopt/request/" + adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("adoptionRequestDTO.comment").value("Approved, ready to be adopted"))
                .andExpect(jsonPath("adoptionRequestDTO.status").value("APPROVED"))
                .andExpect(jsonPath("adoptionRequestDTO.client").value("customer"))
                .andExpect(jsonPath("shelterNetNotificationStatus").value("OK"));


    }

    @Test
    public void bondAnimal() throws Exception {
        animalRepository.saveAll(animalsEntities);

        mockMvc.perform(patch("/bondedanimal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(1, 2, 3))))
                .andExpect(status().isOk());

        List<String> expected = new ArrayList<>();
        expected.add("2");
        expected.add("3");
        mockMvc.perform(get("/animal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shelternateId").value("1"))
                .andExpect(jsonPath("$.animalName").value("cat1"))
                .andExpect(jsonPath("$.species").value("CAT"))
                .andExpect(jsonPath("$.birthDate").value("2015-03-23"))
                .andExpect(jsonPath("$.sex").value("FEMALE"))
                .andExpect(jsonPath("$.color").value("BLACK"))
                .andExpect(jsonPath("$.bond").value(expected));



    }

    @Test
    public void denyAdoptionRequestInSeparable() throws Exception {
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(put("/adopt/request/" + adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
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
                new Animal("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        List.of("2"), ""),
                new Animal("3", "cat2", "CAT", LocalDate.of(2016, 03, 23), "MALE", "BROWN", new ArrayList<>(), "")
        );
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(put("/adopt/request/" + adoptionRequest.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(processRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("adoptionRequestDTO.comment").value("Denied, Can't be adopted"))
                .andExpect(jsonPath("adoptionRequestDTO.status").value("DENIED"))
                .andExpect(jsonPath("adoptionRequestDTO.client").value("customer"))
                .andExpect(jsonPath("shelterNetNotificationStatus").doesNotExist());


    }


    @Test
  public void returnRequestedAnimalToShelter() throws Exception {
        animalRepository.saveAll(animalsEntities);
        List<AnimalReturnDto> expected = List.of(new AnimalReturnDto("1", "Bob is super friendly"), new AnimalReturnDto("2", "Seems to have fleas"));


        MvcResult mvcResult = mockMvc.perform(delete("/animals/return-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(animalsEntities.get(0).getShelternateId(), animalsEntities.get(1).getShelternateId()))))
                .andExpect(status().isOk())
                .andReturn();
        List<AnimalReturnDto> actualResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<AnimalReturnDto>>() {});
        assertEquals(expected, actualResponse);
    }


    @Test
    public void carryItemToStoreCatalog() throws Exception {

       mockMvc.perform(post("/storeCatalog/carry").contentType(MediaType.APPLICATION_JSON)
       .content(mapper.writeValueAsString(storeItems.get(0))))
               .andExpect(status().isAccepted())
               .andExpect(jsonPath("sku").value(1))
               .andExpect(jsonPath("itemCategory").value("FOOD"))
               .andExpect(jsonPath("animalType").value("CAT"))
               .andExpect(jsonPath("brand").value("Brand"))
               .andExpect(jsonPath("name").value("SomeFood"))
               .andExpect(jsonPath("description").value("Food for cats"))
               .andExpect(jsonPath("price").value("9.99"));
    }


    @Test
    public void addItemToStoreCatalog() throws Exception {


        StoreItem storeItem = storeItemRepository.save(storeItems.get(0));
        int quantity = 5;
        mockMvc.perform(post("/storeCatalog/add/"+ storeItem.getId()+"/"+quantity))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("sku").value(1))
                .andExpect(jsonPath("itemCategory").value("FOOD"))
                .andExpect(jsonPath("animalType").value("CAT"))
                .andExpect(jsonPath("brand").value("Brand"))
                .andExpect(jsonPath("name").value("SomeFood"))
                .andExpect(jsonPath("description").value("Food for cats"))
                .andExpect(jsonPath("price").value("9.99"))
                .andExpect(jsonPath("quantity").value(15));
    }

    @Test
    public void purchaseItemFromStoreWithCredit() throws Exception {
        storeItemRepository.saveAll(storeItems);
        List<ItemPurchaseRequest> itemPurchaseRequestList = List.of(new ItemPurchaseRequest(1l, 4), new ItemPurchaseRequest(2l, 10));
        mockMvc.perform(patch("/storeCatalog/purchaseItem/credit/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(itemPurchaseRequestList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(89.86000000000001));

       assertEquals(6, storeItemRepository.findBySku(1l).getQuantity());
       assertEquals(5, storeItemRepository.findBySku(2l).getQuantity());
    }
}
