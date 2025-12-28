package com.order.service.order_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
