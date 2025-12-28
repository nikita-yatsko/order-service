package com.order.service.order_service.model.entity;

import com.order.service.order_service.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false)
    private Status status;

    @Column(name = "total_price", nullable = false)
    @Min(value = 0)
    private Double totalPrice;

    @Column(nullable = false)
    private Boolean deleted;

    @OneToMany(mappedBy = "order",  fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
