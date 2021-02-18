package com.petstore.repository;

import com.petstore.model.AdoptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest,Long> {
}
