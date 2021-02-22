package com.petstore.repository;

import com.petstore.model.StoreItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreItemRepository extends JpaRepository<StoreItem,Long> {

}
