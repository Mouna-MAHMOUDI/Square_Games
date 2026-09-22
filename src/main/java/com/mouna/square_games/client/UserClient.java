package com.mouna.square_games.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class UserClient {
    private final RestClient restClient;

    public UserClient(
            RestClient.Builder restClientBuilder,
            @Value("${users.api.url}") String usersApiUrl){

    this.restClient = restClientBuilder.baseUrl(usersApiUrl).build();
    }

    public boolean userExists(UUID userId) {
        return restClient
                .get()
                .uri("/users/{id}/valid", userId)
                .retrieve()
                .body(Boolean.class);
    }
}
