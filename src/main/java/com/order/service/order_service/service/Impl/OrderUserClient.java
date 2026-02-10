package com.order.service.order_service.service.Impl;

import com.order.service.order_service.model.dto.UserInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrderUserClient {

    private final WebClient webClient;

    public OrderUserClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://user-service:8083").build();
    }

    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "fallback")
    public UserInfo getUserByEmail(Integer userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/api/user/info/{userId}")
                    .build(userId))
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
    }

    public UserInfo fallback(Integer userId, Throwable ex) {
        return new UserInfo("Unknown", "Unknown", null, "Unknown");
    }
}
