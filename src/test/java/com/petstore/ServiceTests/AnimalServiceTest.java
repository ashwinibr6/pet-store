package com.petstore.ServiceTests;

import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ItemPurchaseRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.dto.AnimalReturnDto;
import com.petstore.dto.StoreItemDTO;
import com.petstore.exception.ItemNotFoundException;
import com.petstore.model.*;
import com.petstore.repository.AdoptionRequestRepository;
import com.petstore.repository.AnimalRepository;
import com.petstore.repository.StoreItemRepository;
import com.petstore.service.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {

    @Mock
    AnimalRepository animalRepository;

    @Mock
    StoreItemRepository storeItemRepository;

    @Mock
    AdoptionRequestRepository adoptionRequestRepository;

    @InjectMocks
    AnimalService animalService;

    List<AnimalDTO> animalsDTO;
    List<Animal> animals;
    List<StoreItem> storeItems;
    List<StoreItemDTO> storeItemDTOs;


    @BeforeEach
    void setUp() {
        animalsDTO = List.of(
                new AnimalDTO("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        List.of("2"), "Bob is super friendly"),
                new AnimalDTO("2", "cat3", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        List.of("1"), "Seems to have fleas")
        );

        animals = List.of(
                new Animal("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        List.of("2"), "Bob is super friendly"),
                new Animal("2", "cat3", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        List.of("1"), "Seems to have fleas")
        );

        storeItems=List.of(new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood",
                "Food for cats",9.99, 10),
                new StoreItem(2L, ItemCategory.TOYS.name(),AnimalType.DOG.name(),"Brand","Toy","Toy for dog",4.99, 15),
                new StoreItem(3L, ItemCategory.HOMES.name(),AnimalType.DOG.name(),"Brand","Home","Home for dog",20.99, 30));
        storeItemDTOs=List.of(new StoreItemDTO(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood",
                        "Food for cats",9.99, 10),
                new StoreItemDTO(2L, ItemCategory.TOYS.name(),AnimalType.DOG.name(),"Brand","Toy","Toy for dog",4.99, 15),
                new StoreItemDTO(3L, ItemCategory.HOMES.name(),AnimalType.DOG.name(),"Brand","Home","Home for dog",20.99, 30));
    }

    @Test
    public void getAllAnimals() {
        List<Animal> expected = new ArrayList<>();
        expected.add(animals.get(0));
        expected.add(animals.get(1));

        when(animalRepository.findAll()).thenReturn(expected);
        List<AnimalDTO> actual = animalService.getAnimals();
        verify(animalRepository, times(1)).findAll();

        assertEquals(expected.get(0).getShelternateId(), actual.get(0).getShelternateId());
        assertEquals(expected.get(0).getAnimalName(), actual.get(0).getAnimalName());
        assertEquals(expected.get(0).getBirthDate(), actual.get(0).getBirthDate());
        assertEquals(expected.get(0).getColor(), actual.get(0).getColor());
        assertEquals(expected.get(0).getSex(), actual.get(0).getSex());
        assertEquals(expected.get(0).getSpecies(), actual.get(0).getSpecies());
        assertEquals(expected.size(), actual.size());
    }

    @Test
    public void addAnimals() throws Exception {
        when(animalRepository.saveAll(animals)).thenReturn(animals);
        List<AnimalDTO> actual = animalService.addAnimals(animalsDTO);
        assertEquals(animalsDTO, actual);
    }

    @Test
    public void createAdoptionRequest() {
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer", animalsDTO, Status.PENDING.name(), "");
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animals, Status.PENDING.name());

        when(animalRepository.findByShelternateId("1")).thenReturn(animals.get(0));
        when(animalRepository.findByShelternateId("2")).thenReturn(animals.get(1));
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);

        List<String> shelterNetIds = List.of("1", "2");
        CustomerRequest customerRequest = new CustomerRequest("customer", shelterNetIds);
        AdoptionRequestDTO actual = animalService.createAdoptionRequest(customerRequest);
        assertEquals(adoptionRequestDTO, actual);
        verify(animalRepository, times(2)).findByShelternateId(any());
    }

    @Test
    public void returnAnimalToShelter() {
        animalService.removeAnimals(List.of("1"));
        verify(animalRepository, times(1)).deleteAnimalByShelternateId("1");
    }

    @Test
    public void getAnimalByShelterId() {
        when(animalRepository.findByShelternateId("1")).thenReturn(animals.get(0));
        AnimalDTO actual = animalService.getAnimal("1");
        verify(animalRepository, times(1)).findByShelternateId("1");
        assertEquals(animalsDTO.get(0), actual);
    }

    @Test
    public void approveAdoptionRequest() {
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer", animalsDTO, Status.APPROVED.name(), "Approved, ready to be adopted");
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animals, Status.PENDING.name());
        when(adoptionRequestRepository.getOne(any())).thenReturn(adoptionRequest);
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");


        AdoptionRequestDTO actual = animalService.manageRequest(1l, processRequest);
        verify(animalRepository, times(2)).deleteAnimalByShelternateId(any());

        assertEquals(adoptionRequestDTO, actual);

    }


    @Test
    public void bondAnimal() {
        when(animalRepository.findByShelternateId("1")).thenReturn(animals.get(0));
        when(animalRepository.findByShelternateId("2")).thenReturn(animals.get(1));

        animalService.bondAnimals(Arrays.asList("1", "2"));
        AnimalDTO actual = animalService.getAnimal("1");
        assertEquals(List.of("2"), actual.getBond());
    }

    @Test
    public void denyAdoptionRequestInSeparable() {
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer", animalsDTO, Status.DENIED.name(), "Denied, Can't be adopted");
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animals, Status.PENDING.name());
        when(adoptionRequestRepository.getOne(any())).thenReturn(adoptionRequest);
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");


        AdoptionRequestDTO actual = animalService.manageRequest(1l, processRequest);
        verify(animalRepository, times(0)).deleteAnimalByShelternateId(any());

        assertEquals(adoptionRequestDTO, actual);

    }

    @Test
    public void denyAdoptionRequestNonSeparable() {
        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        List.of("2"), ""),
                new AnimalDTO("3", "cat3", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        new ArrayList<>(), "")
        );
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer", animals, Status.DENIED.name(), "Denied, Can't be adopted");
        List<Animal> animalsEntities = List.of(
                new Animal("1", "cat1", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        List.of("2"), ""),
                new Animal("3", "cat3", "CAT", LocalDate.of(2015, 03, 23), "FEMALE", "BLACK",
                        new ArrayList<>(), "")
        );
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer", animalsEntities, Status.PENDING.name());
        when(adoptionRequestRepository.getOne(any())).thenReturn(adoptionRequest);
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");

        AdoptionRequestDTO actual = animalService.manageRequest(1l, processRequest);
        verify(animalRepository, times(0)).deleteAnimalByShelternateId(any());

        assertEquals(adoptionRequestDTO, actual);
    }

    @Test
    public void returnRequestedAnimalToShelter(){
        when(animalRepository.findByShelternateId("1")).thenReturn(animals.get(0));
        when(animalRepository.findByShelternateId("2")).thenReturn(animals.get(1));

        List<AnimalReturnDto> expected = List.of(new AnimalReturnDto("1", "Bob is super friendly"), new AnimalReturnDto("2", "Seems to have fleas"));
        List<AnimalReturnDto> actual = animalService.returnRequestedAnimalToShelter(List.of("1", "2"));

        verify(animalRepository,times(2)).deleteAnimalByShelternateId(any());
        assertEquals(expected,actual);
    }

    @Test
    public void carryStoreItem(){
        StoreItem storeItem=new StoreItem(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood","Food for cats",9.99);
        StoreItemDTO storeItemDTO=new StoreItemDTO(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),"Brand","SomeFood","Food for cats",9.99);
        when(storeItemRepository.save(storeItem)).thenReturn(storeItem);
        StoreItemDTO actual=animalService.carryItem(storeItemDTO);
        assertEquals(storeItemDTO,actual);
        verify(storeItemRepository).save(storeItem);
    }

    @Test
    public void addItemQuantity(){
        storeItemDTOs.get(0).setQuantity(15);
        when(storeItemRepository.save(any())).thenReturn(storeItems.get(0));
        when(storeItemRepository.getOne(any())).thenReturn(storeItems.get(0));

        StoreItemDTO actual = animalService.addItemQuantity(1l, 5);

        assertEquals(storeItemDTOs.get(0), actual);
        verify(storeItemRepository).save(storeItems.get(0));
        verify(storeItemRepository).getOne(any());

    }
    @Test
    public void searchAccessories(){
        List<StoreItem> storeItems = List.of(
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

        when(storeItemRepository.findAll()).thenReturn(storeItems);


        List<StoreItemDTO> actual = animalService.searchAccessories("sku", "1");

        assertEquals(List.of(
                new StoreItemDTO(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 10)
        ), actual);

        actual = animalService.searchAccessories("category", "FOOD", "animal", "cat");

        assertEquals(List.of(
                new StoreItemDTO(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 10),
                new StoreItemDTO(4L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 76)

        ), actual);

        actual = animalService.searchAccessories("animal", "cat","category", "FOOD" );

        assertEquals(List.of(
                new StoreItemDTO(1L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 10),
                new StoreItemDTO(4L, ItemCategory.FOOD.name(),AnimalType.CAT.name(),
                        "Brand","SomeFood","Food for cats",9.99, 76)

        ), actual);

    }
    @Test
    public void searchItemNotExist_BadURL(){
        List<StoreItem> storeItems = List.of(
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

        when(storeItemRepository.findAll()).thenReturn(storeItems);
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                ()->animalService.searchAccessories("skuu","1"));
        assertEquals("Item not found or bad URL",exception.getMessage());
        exception=assertThrows(ItemNotFoundException.class,
                ()->animalService.searchAccessories("categories", "FOOD", "animal", "cat"));
        assertEquals("Item not found or bad URL",exception.getMessage());
    }


    @Test
    public void purchaseItemFromStoreWithCredit(){
        List<ItemPurchaseRequest> itemPurchaseRequestList = List.of(new ItemPurchaseRequest(1l, 4), new ItemPurchaseRequest(2l, 10));
        when(storeItemRepository.findBySku(1l)).thenReturn(storeItems.get(0));
        when(storeItemRepository.findBySku(2l)).thenReturn(storeItems.get(1));

        double actual = animalService.purchaseItemFromStoreWithCredit(itemPurchaseRequestList);
        assertEquals(89.86, actual, 0.1);
        verify(storeItemRepository, times(2)).save(any());
    }


}

