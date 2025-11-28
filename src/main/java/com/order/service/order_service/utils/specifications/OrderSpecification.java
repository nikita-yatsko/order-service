package com.order.service.order_service.utils.specifications;

import com.order.service.order_service.model.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> dateBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }
}
