package com.kharlamova.auth_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserServiceClient {
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserResponse createUser(UserRequest request) {
        return restTemplate.postForObject(
                userServiceUrl + "/users",
                request,
                UserResponse.class
        );
    }

    public void rollbackUserCreation(Long userId) {
        restTemplate.delete(
                userServiceUrl + "/users/" + userId
        );
    }
}
