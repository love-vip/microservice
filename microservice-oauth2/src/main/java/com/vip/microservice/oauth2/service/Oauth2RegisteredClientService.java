package com.vip.microservice.oauth2.service;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Optional;
public interface Oauth2RegisteredClientService {

    Optional<RegisteredClient> findByClientId(String clientId);
}