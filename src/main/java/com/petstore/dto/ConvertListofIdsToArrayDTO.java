package com.petstore.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class ConvertListofIdsToArrayDTO {

    private List<Integer> animalIds;


}
