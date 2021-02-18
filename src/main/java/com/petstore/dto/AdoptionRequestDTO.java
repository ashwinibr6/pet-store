package com.petstore.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class AdoptionRequestDTO {

    private String client;

    private List<AnimalDTO> animalDTOS;


}
