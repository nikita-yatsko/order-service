package com.order.service.order_service.repository;

import com.order.service.order_service.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    Optional<Item> findItemById(Integer id);

    Optional<Item> findByName(String name);

}
