package com.order.service.order_service.service;

import com.order.service.order_service.model.dto.UserInfo;

public interface UserCacheService {

    UserInfo getUserInfo(String email);
}
