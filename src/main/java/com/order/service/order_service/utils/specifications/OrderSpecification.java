package com.order.service.order_service.utils.specifications;

import com.order.service.order_service.model.entity.Order;
import com.order.service.order_service.model.enums.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> dateBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {

            Predicate predicate;

            if (from == null && to == null) {
                predicate = cb.conjunction();
            } else if (from != null && to != null) {
                predicate = cb.between(root.get("createdAt"), from, to);
            } else if (from != null) {
                predicate = cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            } else {
                predicate = cb.lessThanOrEqualTo(root.get("createdAt"), to);
            }

            return predicate;
        };
    }

    public static Specification<Order> filterByStatus(String statusSort) {
        return (root, query, cb) -> {
            if (statusSort == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), Status.valueOf(statusSort));
        };
    }
}
