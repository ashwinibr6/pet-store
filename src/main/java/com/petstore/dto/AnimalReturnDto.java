package com.petstore.dto;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class AnimalReturnDto {
    private String id;
    private String note;

    public AnimalReturnDto(String id, String note) {
        this.id=id;
        this.note=note;
    }
}
