package com.order.service.order_service.service.Impl;

import com.order.service.order_service.model.dto.UserInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderUserClient {

    private final RestTemplate restTemplate;

    public OrderUserClient(RestTemplateBuilder builder,
                           @Value("${user.service.url}") String baseUrl) {
        this.restTemplate = builder.rootUri(baseUrl).build();
    }

    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "fallback")
    public UserInfo getUserByEmail(String email) {
        return restTemplate.getForObject("/api/user/info/{email}", UserInfo.class, email);
    }

    public UserInfo fallback(String email, Throwable ex) {
        return new UserInfo("Unknow", "Unknow", null, email);
    }
}
