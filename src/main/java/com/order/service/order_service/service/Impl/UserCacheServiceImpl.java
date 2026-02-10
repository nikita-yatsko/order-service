package com.order.service.order_service.service.Impl;

import com.order.service.order_service.model.dto.UserInfo;
import com.order.service.order_service.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheServiceImpl implements UserCacheService {

    private final OrderUserClient orderUserClient;

    //@Cacheable(value = "userInfo", key  = "#userId")
    public UserInfo getUserInfo(Integer userId) {
        return orderUserClient.getUserByEmail(userId);
    }
}
