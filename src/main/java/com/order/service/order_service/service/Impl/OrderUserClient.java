package com.order.service.order_service.service.Impl;

import com.order.service.order_service.model.dto.UserInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrderUserClient {

    private final WebClient webClient;

    public OrderUserClient(WebClient.Builder builder,
                           @Value("${user.service.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "fallback")
    public UserInfo getUserByEmail(String email) {
        return webClient.get()
                .uri("/api/user/info/{email}", email)
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
    }

    public UserInfo fallback(String email, Throwable ex) {
        return new UserInfo("Unknow", "Unknow", null, email);
    }
}
