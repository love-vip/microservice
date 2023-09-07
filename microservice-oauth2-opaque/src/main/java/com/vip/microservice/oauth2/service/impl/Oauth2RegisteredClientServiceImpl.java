package com.vip.microservice.oauth2.service.impl;

import com.vip.microservice.oauth2.service.Oauth2RegisteredClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Oauth2RegisteredClientServiceImpl implements Oauth2RegisteredClientService {

    private final RegisteredClientRepository registeredClientRepository;

    @Override
    public Optional<RegisteredClient> findByClientId(String clientId) {
        return Optional.ofNullable(registeredClientRepository.findByClientId(clientId));
    }
}
