package com.petstore.RestDocs;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ItemPurchaseRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.StoreItemDTO;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.AnimalReturnDto;
import com.petstore.dto.StoreItemDTO;
import com.petstore.model.*;
import com.petstore.service.AnimalService;
import com.petstore.service.ShelterNetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/target/snippets")
public class PetStoreRestDocs {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper;

    @MockBean
    AnimalService animalService;

    @MockBean
    ShelterNetService shelterNetService;

    List<Animal> animalsEntities;
    List<StoreItem> storeItems;
    List<AnimalDTO> animalsDTO;
    AdoptionRequestDTO adoptionRequestDTO;
    List<AnimalReturnDto> animalReturnDtos;
    List<StoreItemDTO> storeItemDTOS;

    @BeforeEach
    public void setUp(){
        mapper = new ObjectMapper();

       animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>(),""),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>(),"")
        );

        storeItems = List.of(new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood","Food for cats",9.99, 10),
                new StoreItem(2L, ItemCategory.TOYS.name(),AnimalType.DOG.name(),"Brand","Toy","Toy for dog",4.99, 15),
                new StoreItem(3L, ItemCategory.HOMES.name(),AnimalType.DOG.name(),"Brand","Home","Home for dog",20.99, 30));

        animalsDTO = List.of(
                new AnimalDTO("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK", List.of("2", "3"), "Bob is super friendly"),
                new AnimalDTO("2", "cat2", "CAT", LocalDate.of(2016, 03, 23), "MALE", "BROWN", List.of("1", "3"), "Seems to have fleas")
        );

        adoptionRequestDTO = new AdoptionRequestDTO("customer", List.of(animalsDTO.get(0), animalsDTO.get(1)), Status.PENDING.toString(), "");

        animalReturnDtos =  List.of(new AnimalReturnDto("1", "Bob is super friendly"), new AnimalReturnDto("2", "Seems to have fleas"));

        storeItemDTOS = List.of(new StoreItemDTO(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 10),
                new StoreItemDTO(2L, ItemCategory.TOYS.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",4.99, 15),
                new StoreItemDTO(4L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",20.99, 76),
                new StoreItemDTO(3L, ItemCategory.HOMES.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",6.59, 34),
                new StoreItemDTO(8L, ItemCategory.FOOD.name(),AnimalType.DOG.name(),
                        "Brand","SomeFood","Food for cats",2.49, 49));
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

        when(animalService.addAnimals(any())).thenReturn(animalsDTO);
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
        when(animalService.createAdoptionRequest(any())).thenReturn(adoptionRequestDTO);
        List<String> shelterNetIds = List.of(animalsDTO.get(0).getShelternateId(),animalsDTO.get(1).getShelternateId());

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
        when(animalService.getAnimals()).thenReturn(animalsDTO);
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
        when(shelterNetService.returnAnimalToShelter(any())).thenReturn(HttpStatus.OK);
        List<String> animalsIds = List.of("1","2");

        mockMvc
                .perform(delete("/animalreturns").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(animalsIds))).andExpect(status().isOk())
                .andDo(document("returnAnimalToShelter"));
    }

    @Test
    public void returnSickAnimalToShelter() throws Exception {
        when(animalService.getAnimal(any())).thenReturn(animalsDTO.get(0));
        when(shelterNetService.returnSickAnimalToShelter(any(), any())).thenReturn(HttpStatus.OK);

        mockMvc
                .perform(delete("/sickanimal/?shelternateId=1&diagnosis=fever"))
                        .andExpect(status().isOk())
                .andDo(document("returnSickAnimalToShelter"));
    }
    @Test
    public void bondAnimal() throws Exception {
        mockMvc.perform(patch("/bondedanimal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(1,2))))
                .andExpect(status().isOk()).andDo(document("bondedanimal"));
    }
    @Test
    public void getAnimal() throws Exception {
    when(animalService.getAnimal(any())).thenReturn(animalsDTO.get(0));
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
        adoptionRequestDTO = new AdoptionRequestDTO("customer", List.of(animalsDTO.get(0), animalsDTO.get(1)), Status.APPROVED.toString(), "APPROVED");
        when(animalService.manageRequest(any(), any())).thenReturn(adoptionRequestDTO);
        when(shelterNetService.notifyAnimalAdoption(any())).thenReturn(HttpStatus.OK);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");

        mockMvc
                .perform(RestDocumentationRequestBuilders.put("/adopt/request/{id}",1).contentType(MediaType.APPLICATION_JSON)
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
        adoptionRequestDTO = new AdoptionRequestDTO("customer", List.of(animalsDTO.get(0), animalsDTO.get(1)), Status.DENIED.toString(), "DENIED");
        when(animalService.manageRequest(any(), any())).thenReturn(adoptionRequestDTO);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(RestDocumentationRequestBuilders.put("/adopt/request/{id}",1).contentType(MediaType.APPLICATION_JSON)
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
        animalsDTO = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2"),""),
                new AnimalDTO("3","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>(),"")
        );
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer",animalsDTO, Status.PENDING.toString(),"DENIED");
        when(animalService.manageRequest(any(), any())).thenReturn(adoptionRequestDTO);

        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");

        mockMvc
                .perform(RestDocumentationRequestBuilders.put("/adopt/request/{id}",1).contentType(MediaType.APPLICATION_JSON)
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
    public void returnRequestedAnimalToShelter() throws Exception{
        when(animalService.returnRequestedAnimalToShelter(any())).thenReturn(animalReturnDtos);
        mockMvc.perform(delete("/animals/return-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(animalsDTO.get(0).getShelternateId(), animalsDTO.get(1).getShelternateId()))))
                .andExpect(status().isOk())
                .andDo(document("returnRequestedAnimalToShelter",responseFields(
                        fieldWithPath("[].id").description("ShelterNetId of Animal"),
                        fieldWithPath("[].note").description("Animal note"))));
    }

    @Test
    public void carryItemToStoreCatalog() throws Exception {
        when(animalService.carryItem(any())).thenReturn(storeItemDTOS.get(0));

        mockMvc.perform(post("/storeCatalog/carry").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(storeItemDTOS.get(0))))
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
        when(animalService.addItemQuantity(1, 5)).thenReturn(storeItemDTOS.get(0));
        mockMvc.perform(RestDocumentationRequestBuilders.post("/storeCatalog/add/{id}/{quantity}",1,5))
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

        when(animalService.searchAccessories("sku","1")).thenReturn(List.of(storeItemDTOS.get(0)));
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
        when(animalService.searchAccessories("animal","CAT","category","TOYS"))
                .thenReturn(List.of(storeItemDTOS.get(1)));
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

    @Test
    public void purchaseItemFromStoreWithCredit() throws Exception {
        when(animalService.purchaseItemFromStoreWithCredit(any())).thenReturn(89.86000000000001);
        List<ItemPurchaseRequest> itemPurchaseRequestList = List.of(new ItemPurchaseRequest(1l, 4), new ItemPurchaseRequest(2l, 10));
        mockMvc.perform(patch("/storeCatalog/purchaseItem/credit/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(itemPurchaseRequestList)))
                .andExpect(status().isOk())
                .andDo(document("PurchaseItemFromStoreWithCredit"));
    }
}
