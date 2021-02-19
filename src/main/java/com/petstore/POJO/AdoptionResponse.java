package com.petstore.POJO;

import com.petstore.dto.AdoptionRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class AdoptionResponse {
    HttpStatus shelterNetNotificationStatus;
    AdoptionRequestDTO adoptionRequestDTO;

}
