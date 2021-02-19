package com.petstore.ServiceTests;

import com.petstore.POJO.CustomerRequest;
import com.petstore.POJO.ProcessAdoptionRequest;
import com.petstore.dto.AdoptionRequestDTO;
import com.petstore.dto.AnimalDTO;
import com.petstore.model.AdoptionRequest;
import com.petstore.model.Animal;
import com.petstore.model.Status;
import com.petstore.repository.AdoptionRequestRepository;
import com.petstore.repository.AnimalRepository;
import com.petstore.service.AnimalService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {

    @Mock
    AnimalRepository animalRepository;

    @Mock
    AdoptionRequestRepository adoptionRequestRepository;

    @InjectMocks
    AnimalService animalService;

    @Test
    public void getAllAnimals(){

        Animal animal1=new Animal("101","Lion1","species", LocalDate.of(2015,12,27)
                , "Male","Gold", new ArrayList<>());

        Animal animal2=new Animal("102","Lion1","species", LocalDate.of(2015,12,27)
                , "Male","Gold", new ArrayList<>());

        List<Animal> expected=new ArrayList<>();
        expected.add(animal1);
        expected.add(animal2);

        when(animalRepository.findAll()).thenReturn(expected);
        List<AnimalDTO> actual=animalService.getAnimals();
        verify(animalRepository, times(1)).findAll();

        assertEquals(expected.get(0).getShelternateId(),actual.get(0).getShelternateId());
        assertEquals(expected.get(0).getAnimalName(),actual.get(0).getAnimalName());
        assertEquals(expected.get(0).getBirthDate(),actual.get(0).getBirthDate());
        assertEquals(expected.get(0).getColor(),actual.get(0).getColor());
        assertEquals(expected.get(0).getSex(),actual.get(0).getSex());
        assertEquals(expected.get(0).getSpecies(),actual.get(0).getSpecies());
        assertEquals(expected.size(),actual.size());
    }

    @Test
    public void addAnimals() throws Exception {
        List<AnimalDTO> animalsDto = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",new ArrayList<>()),
                new AnimalDTO( "2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",new ArrayList<>()),
                new AnimalDTO( "3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW",new ArrayList<>()),
                new AnimalDTO("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE",new ArrayList<>()),
                new AnimalDTO( "5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN",new ArrayList<>())
        );

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>()),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>()),
                new Animal("3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW", new ArrayList<>()),
                new Animal("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE", new ArrayList<>()),
                new Animal("5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN", new ArrayList<>())
        );
        when(animalRepository.saveAll(animalsEntities)).thenReturn(animalsEntities);
        List<AnimalDTO> actual = animalService.addAnimals(animalsDto);
        assertEquals(animalsDto, actual);
    }

    @Test
    public void createAdoptionRequest(){

        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",new ArrayList<>()),
                new AnimalDTO("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",new ArrayList<>())
        );
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer",animals, Status.PENDING.name(),"");
        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>()),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>())
         );
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.name());

        when(animalRepository.findByShelternateId("1")).thenReturn(animalsEntities.get(0));
        when(animalRepository.findByShelternateId("2")).thenReturn(animalsEntities.get(1));
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);

        List<String> shelterNetIds = List.of("1","2");
        CustomerRequest customerRequest = new CustomerRequest("customer", shelterNetIds);
        AdoptionRequestDTO actual = animalService.createAdoptionRequest(customerRequest);
        assertEquals(adoptionRequestDTO, actual);
        verify(animalRepository, times(2)).findByShelternateId(any());
    }

    @Test
    public void returnAnimalToShelter(){
        animalService.removeAnimals(List.of("101"));
        verify(animalRepository, times(1)).deleteAnimalByShelternateId("101");
    }

    @Test
    public void getAnimalByShelterId(){
        Animal animal=new Animal("101","cat1","CAT",
                LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>());
        AnimalDTO animalDto=new AnimalDTO("101","cat1","CAT",
                LocalDate.of(2015,03,23),"FEMALE","BLACK",new ArrayList<>());
        when(animalRepository.findByShelternateId("101")).thenReturn(animal);
        AnimalDTO actual=animalService.getAnimal("101");
        verify(animalRepository,times(1)).findByShelternateId("101");
        assertEquals(animalDto,actual);
    }

    @Test
    public void approveAdoptionRequest(){

        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new AnimalDTO("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",
                        List.of("1")),
                new AnimalDTO("3","cat3","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                                new ArrayList<>())
        );
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer",animals, Status.APPROVED.name(), "Approved, ready to be adopted");
        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",
                        List.of("1")),
                new Animal("3","cat3","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        new ArrayList<>())
        );
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.name());
        when(adoptionRequestRepository.getOne(any())).thenReturn(adoptionRequest);
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");


        AdoptionRequestDTO actual = animalService.manageRequest(1l, processRequest);
        verify(animalRepository, times(3)).deleteAnimalByShelternateId(any());

        assertEquals(adoptionRequestDTO, actual);

    }


    @Test
    public void bondAnimal(){

        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK", new ArrayList<>()),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN", new ArrayList<>()),
                new Animal("3","dog1","DOG",LocalDate.of(2017,03,23),"FEMALE","YELLOW", new ArrayList<>()),
                new Animal("4","dog4","DOG", LocalDate.of(2015,03,23),"MALE","WHITE", new ArrayList<>()),
                new Animal("5","bird","BIRD", LocalDate.of(2015,03,23),"FEMALE","GREEN", new ArrayList<>())
        );


        when(animalRepository.findByShelternateId("1")).thenReturn(animalsEntities.get(0));
        when(animalRepository.findByShelternateId("2")).thenReturn(animalsEntities.get(1));
        when(animalRepository.findByShelternateId("3")).thenReturn(animalsEntities.get(2));

        animalService.bondAnimals(Arrays.asList("1","2","3"));
        AnimalDTO actual=animalService.getAnimal("1");
        assertEquals(List.of("2","3"),actual.getBond());

    }

    @Test
    public void denyAdoptionRequestInSeparable(){

        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new AnimalDTO("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",
                        List.of("1")),
                new AnimalDTO("3","cat3","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        new ArrayList<>())
        );
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer",animals, Status.DENIED.name(), "Denied, Can't be adopted");
        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new Animal("2","cat2","CAT",LocalDate.of(2016,03,23),"MALE","BROWN",
                        List.of("1")),
                new Animal("3","cat3","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        new ArrayList<>())
        );
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.name());
        when(adoptionRequestRepository.getOne(any())).thenReturn(adoptionRequest);
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.DENIED.toString(), "Denied, Can't be adopted");


        AdoptionRequestDTO actual = animalService.manageRequest(1l, processRequest);
        verify(animalRepository, times(0)).deleteAnimalByShelternateId(any());

        assertEquals(adoptionRequestDTO, actual);

    }

    @Test
    public void denyAdoptionRequestNonSeparable(){

        List<AnimalDTO> animals = List.of(
                new AnimalDTO("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new AnimalDTO("3","cat3","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        new ArrayList<>())
        );
        AdoptionRequestDTO adoptionRequestDTO = new AdoptionRequestDTO("customer",animals, Status.DENIED.name(), "Denied, Can't be adopted");
        List<Animal> animalsEntities = List.of(
                new Animal("1","cat1","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        List.of("2")),
                new Animal("3","cat3","CAT", LocalDate.of(2015,03,23),"FEMALE","BLACK",
                        new ArrayList<>())
        );
        AdoptionRequest adoptionRequest = new AdoptionRequest("customer",animalsEntities, Status.PENDING.name());
        when(adoptionRequestRepository.getOne(any())).thenReturn(adoptionRequest);
        when(adoptionRequestRepository.save(adoptionRequest)).thenReturn(adoptionRequest);
        ProcessAdoptionRequest processRequest = new ProcessAdoptionRequest(Status.APPROVED.toString(), "Approved, ready to be adopted");

        AdoptionRequestDTO actual = animalService.manageRequest(1l, processRequest);
        verify(animalRepository, times(0)).deleteAnimalByShelternateId(any());

        assertEquals(adoptionRequestDTO, actual);

    }
}

