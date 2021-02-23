package com.petstore.RestDocs;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.StoreItemDTO;
import com.petstore.model.*;
import com.petstore.repository.AdoptionRequestRepository;
import com.petstore.repository.AnimalRepository;
import com.petstore.repository.StoreItemRepository;
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
    StoreItemRepository storeItemRepository;

    @Autowired
    AdoptionRequestRepository adoptionRequestRepository;

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
                        fieldWithPath("comment").description("The comment of the request approval"),
                        fieldWithPath("status").description("The status of the request"),
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
                        fieldWithPath("shelterNetNotificationStatus").description("ShelterNet Notification Status"))));


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
                        fieldWithPath("shelterNetNotificationStatus").description("ShelterNet Notification Status"))));


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
                        fieldWithPath("shelterNetNotificationStatus").description("ShelterNet Notification Status"))));


    }
    @Test
    public void carryItemToStoreCatalog() throws Exception {
        StoreItem storeItem=new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood","Food for cats",9.99);
        mockMvc.perform(post("/storeCatalog/carry").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(storeItem)))
                .andExpect(status().isAccepted())
                .andDo(document("carryItemToStoreCatalog",
                        responseFields(
                        fieldWithPath("sku").description("stocking unit -- ID"),
                        fieldWithPath("itemCategory").description("Item category: FOOD,TOYS,HOMES,CARRIES"),
                        fieldWithPath("animalType").description("Animal typ: CAT,DOG,BIRD"),
                        fieldWithPath("brand").description("The item brand"),
                        fieldWithPath("name").description("The item name"),
                        fieldWithPath("description").description("The item description"),
                        fieldWithPath("quantity").description("The item quantity to be added"),
                        fieldWithPath("price").description("The item price"))));
    }

    @Test
    public void addItemToStoreCatalog() throws Exception {
        StoreItem storeItem=new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood","Food for cats",9.99, 10);

        storeItem = storeItemRepository.save(storeItem);
        int quantity = 5;
        mockMvc.perform(RestDocumentationRequestBuilders.post("/storeCatalog/add/{id}/{quantity}",storeItem.getId(),quantity))
                .andExpect(status().isAccepted())
                .andDo(document("AddItemQuantityToStoreCatalog"
                        ,pathParameters(
                                parameterWithName("id").description("The store Item ID"),
                                parameterWithName("quantity").description("The quantity of items to be added")),
                        responseFields(
                                fieldWithPath("sku").description("stocking unit -- ID"),
                                fieldWithPath("itemCategory").description("Item category: FOOD,TOYS,HOMES,CARRIES"),
                                fieldWithPath("animalType").description("Animal typ: CAT,DOG,BIRD"),
                                fieldWithPath("brand").description("The item brand"),
                                fieldWithPath("name").description("The item name"),
                                fieldWithPath("description").description("The item description"),
                                fieldWithPath("quantity").description("The item quantity to be added"),
                                fieldWithPath("price").description("The item price"))));
    }

    @Test
    public void searchAccessories() throws Exception {

        List<StoreItem> items = List.of(
                new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 10),
                new StoreItem(2L, ItemCategory.TOYS.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 15),
                new StoreItem(4L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 76),
                new StoreItem(3L, ItemCategory.HOMES.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 34),
                new StoreItem(8L, ItemCategory.FOOD.name(),AnimalType.DOG.name(),
                        "Brand","SomeFood","Food for cats",9.99, 49)
        );

        List<StoreItemDTO> storeItems = List.of(
                new StoreItemDTO(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 10),
                new StoreItemDTO(2L, ItemCategory.TOYS.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 15),
                new StoreItemDTO(4L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 76),
                new StoreItemDTO(3L, ItemCategory.HOMES.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 34),
                new StoreItemDTO(8L, ItemCategory.FOOD.name(),AnimalType.DOG.name(),
                        "Brand","SomeFood","Food for cats",9.99, 49)
        );

        storeItemRepository.saveAll(items);
        String searchType = "sku";
        String searchvalue= "1";
        mockMvc
                .perform(RestDocumentationRequestBuilders.get("/items/{searchType}/{searchValue}",searchType,searchvalue))
                .andExpect(status().isOk())
                .andDo(document("searchBySku"
                ,pathParameters(
                        parameterWithName("searchType").description("sku search type"),
                        parameterWithName("searchValue").description("sku ID")),
                responseFields(
                        fieldWithPath("[0].sku").description("stocking unit -- ID"),
                        fieldWithPath("[0].itemCategory").description("Item category: FOOD,TOYS,HOMES,CARRIES"),
                        fieldWithPath("[0].animalType").description("Animal typ: CAT,DOG,BIRD"),
                        fieldWithPath("[0].brand").description("The item brand"),
                        fieldWithPath("[0].name").description("The item name"),
                        fieldWithPath("[0].description").description("The item description"),
                        fieldWithPath("[0].price").description("The item quantity to be added"),
                        fieldWithPath("[0].quantity").description("The item price"))));

        searchType = "animal";
        searchvalue= "CAT";
        String searchAnimalType = "category";
        String searchAnimalValue = "TOYS";
        mockMvc
                .perform(RestDocumentationRequestBuilders.get("/items/{searchType}/{searchValue}/{searchAnimalType}/{searchAnimalValue}",
                        searchType,searchvalue,searchAnimalType,searchAnimalValue))
                .andExpect(status().isOk())
                .andDo(document("searchByCategoryAndAnimalType"
                        ,pathParameters(
                                parameterWithName("searchType").description("category or animal type search"),
                                parameterWithName("searchValue").description("category or animal type search keyword"),
                               parameterWithName("searchAnimalType").description("category or animal type search"),
                               parameterWithName("searchAnimalValue").description("category or animal type search keyword")),

                        responseFields(
                                fieldWithPath("[0].sku").description("stocking unit -- ID"),
                                fieldWithPath("[0].itemCategory").description("Item category: FOOD,TOYS,HOMES,CARRIES"),
                                fieldWithPath("[0].animalType").description("Animal typ: CAT,DOG,BIRD"),
                                fieldWithPath("[0].brand").description("The item brand"),
                                fieldWithPath("[0].name").description("The item name"),
                                fieldWithPath("[0].description").description("The item description"),
                                fieldWithPath("[0].price").description("The item quantity to be added"),
                                fieldWithPath("[0].quantity").description("The item price"))));

    }

}
