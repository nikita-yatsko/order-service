package com.order.service.order_service.repository;

import com.order.service.order_service.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    Optional<Order> findOrderByIdAndDeletedFalse(Integer id);

    Optional<Order> findOrderById(Integer id);

    List<Order> findOrdersByUserIdAndDeletedFalse(Integer userId);

    List<Order> findAllOrdersByUserId(Integer userId);

    @Modifying
    @Query("UPDATE Order o SET o.deleted = true WHERE o.id= :id")
    void softDelete(@Param("id") Integer id);
}
