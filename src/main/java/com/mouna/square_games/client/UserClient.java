package com.mouna.square_games.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class UserClient {
    private final RestClient restClient;
    private final HttpServletRequest request;

    public UserClient(
            RestClient.Builder restClientBuilder,
            @Value("${users.api.url}") String usersApiUrl,
            HttpServletRequest request){

    this.restClient = restClientBuilder.baseUrl(usersApiUrl).build();
    this.request = request;
    }

    public boolean userExists(UUID userId) {
        String authorization = request.getHeader("Authorization");
        return restClient
                .get()
                .uri("/users/{id}/valid", userId)
                .header("Authorization", authorization)
                .retrieve()
                .body(Boolean.class);
    }
}
