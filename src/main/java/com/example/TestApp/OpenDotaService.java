package com.example.TestApp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OpenDotaService {

    @Value("${opendota.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public OpenDotaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getPlayerProfile(String steamId) {
        String url = String.format("%s/players/%s", baseUrl, steamId);
        Map<String, Object> responseBody = restTemplate.getForObject(url, Map.class);

        return responseBody;
    }
}
