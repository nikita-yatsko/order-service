package com.order.service.order_service.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfo {

    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
