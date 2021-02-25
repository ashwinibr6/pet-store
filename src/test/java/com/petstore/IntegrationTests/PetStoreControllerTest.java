package com.petstore.IntegrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ItemPurchaseRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.AnimalReturnDto;
import com.petstore.exception.ItemNotFoundException;
import com.petstore.model.*;
import com.petstore.repository.AdoptionRequestRepository;
import com.petstore.repository.AnimalRepository;
import com.petstore.repository.StoreItemRepository;
import com.petstore.service.ShelterNetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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

    @MockBean
    ShelterNetService shelterNetService;

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

         storeItems = List.of(new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                         "Brand","SomeFood","Food for cats",9.99, 10),
                 new StoreItem(2L, ItemCategory.TOYS.name(),AnimalType.CAT.name(),
                         "Brand","SomeFood","Food for cats",4.99, 15),
                 new StoreItem(4L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                         "Brand","SomeFood","Food for cats",20.99, 76),
                 new StoreItem(3L, ItemCategory.HOMES.name(),AnimalType.CAT.name(),
                         "Brand","SomeFood","Food for cats",6.59, 34),
                 new StoreItem(8L, ItemCategory.FOOD.name(),AnimalType.DOG.name(),
                         "Brand","SomeFood","Food for cats",2.49, 49));

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
        when(shelterNetService.fetchAnimals(animalsIds)).thenReturn(animalsDTO);

        MvcResult result = mockMvc
                .perform(post("/animals")
                        .with(user("user").password("password"))
                        .contentType(MediaType.APPLICATION_JSON)
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
    public void addDuplicateAnimals() throws Exception {
        List<Integer> animalsIds = List.of(1, 2, 3, 4, 5);
        MvcResult result = mockMvc
                .perform(post("/animals")
                        .with(user("user").password("password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult badRequest = mockMvc
                .perform(post("/animals")
                        .with(user("user").password("password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds)))
                .andReturn();
    }

    @Test
    public void returnAnimalToShelter() throws Exception {
        animalRepository.saveAll(animalsEntities);
        when(shelterNetService.returnAnimalToShelter(any())).thenReturn(HttpStatus.OK);
        mockMvc.perform(delete("/animalreturns")
                .with(user("user").password("password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(animalsEntities.get(0).getShelternateId(), animalsEntities.get(1).getShelternateId()))))
                .andExpect(status().isOk());

    }

    @Test
    public void returnSickAnimalToShelter() throws Exception {
        animalRepository.saveAll(animalsEntities);
        when(shelterNetService.returnSickAnimalToShelter(any(),any())).thenReturn(HttpStatus.OK);
        mockMvc.perform(delete("/sickanimal/?shelternateId=1&diagnosis=fever")
                .with(user("user").password("password")))
                .andExpect(status().isOk());
    }

    @Test
    public void approveAdoptionRequest() throws Exception {
        animalsEntities = animalRepository.saveAll(animalsEntities);
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animalsEntities, Status.PENDING.toString());
        adoptionRequest = adoptionRequestRepository.save(adoptionRequest);
        when(shelterNetService.notifyAnimalAdoption(any())).thenReturn(HttpStatus.OK);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");
        mockMvc
                .perform(put("/adopt/request/" + adoptionRequest.getId())
                        .with(user("user").password("password"))
                        .contentType(MediaType.APPLICATION_JSON)
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
                .with(user("user").password("password"))
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
        //when(shelterNetService.notifyAnimalAdoption(any())).thenReturn(HttpStatus.OK);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(put("/adopt/request/" + adoptionRequest.getId())
                        .with(user("user").password("password"))
                        .contentType(MediaType.APPLICATION_JSON)
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
                .perform(put("/adopt/request/" + adoptionRequest.getId())
                        .with(user("user").password("password"))
                        .contentType(MediaType.APPLICATION_JSON)
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
                .with(user("user").password("password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(animalsEntities.get(0).getShelternateId(), animalsEntities.get(1).getShelternateId()))))
                .andExpect(status().isOk())
                .andReturn();
        List<AnimalReturnDto> actualResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<AnimalReturnDto>>() {});
        assertEquals(expected, actualResponse);
    }


    @Test
    public void carryItemToStoreCatalog() throws Exception {

       mockMvc.perform(post("/storeCatalog/carry")
               .with(user("user").password("password"))
               .contentType(MediaType.APPLICATION_JSON)
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
        mockMvc.perform(patch("/items/"+ storeItem.getId()+"/quantity/"+quantity)
                .with(user("user").password("password")))
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
    public void searchAccessories() throws Exception {

        List<StoreItem> items = storeItemRepository.saveAll(storeItems);
        String searchType = "sku";
        String searchvalue= "1";
        mockMvc
                .perform(get("/items/"+searchType+"/"+searchvalue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].sku").value(1))
                .andExpect(jsonPath("$.[0].itemCategory").value("FOOD"))
                .andExpect(jsonPath("$.[0].animalType").value("CAT"))
                .andExpect(jsonPath("$.[0].brand").value("Brand"))
                .andExpect(jsonPath("$.[0].name").value("SomeFood"))
                .andExpect(jsonPath("$.[0].description").value("Food for cats"))
                .andExpect(jsonPath("$.[0].price").value("9.99"))
                .andExpect(jsonPath("$.[0].quantity").value(10));

         searchType = "animal";
         searchvalue= "CAT";
         String searchAnimalType = "category";
         String searchAnimalValue = "TOYS";
         mockMvc
                .perform(get("/items/"+searchType+"/"+searchvalue
                + "/" +searchAnimalType + "/" +searchAnimalValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].sku").value(2))
                .andExpect(jsonPath("$.[0].itemCategory").value("TOYS"))
                .andExpect(jsonPath("$.[0].animalType").value("CAT"))
                .andExpect(jsonPath("$.[0].brand").value("Brand"))
                .andExpect(jsonPath("$.[0].name").value("SomeFood"))
                .andExpect(jsonPath("$.[0].description").value("Food for cats"))
                .andExpect(jsonPath("$.[0].price").value("4.99"))
                .andExpect(jsonPath("$.[0].quantity").value(15));

    }

    @Test
    public void searchAccessories_BadRequest() throws Exception {

        List<StoreItem> items = storeItemRepository.saveAll(storeItems);
        String searchType = "skuu";
        String searchvalue= "1";
        mockMvc
                .perform(get("/items/"+searchType+"/"+searchvalue))
                .andExpect(status().isNotFound())
                .andExpect(result->assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result ->assertEquals("Item not found or bad URL",result.getResolvedException().getMessage()));

        searchType = "animals";
        searchvalue= "CAT";
        String searchAnimalType = "categories";
        String searchAnimalValue = "TOYS";
        mockMvc
                .perform(get("/items/"+searchType+"/"+searchvalue
                        + "/" +searchAnimalType + "/" +searchAnimalValue))
                .andExpect(status().isNotFound())
                .andExpect(result->assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result ->assertEquals("Item not found or bad URL",result.getResolvedException().getMessage()));
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
