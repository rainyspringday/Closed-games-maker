package com.example.TestApp.services;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;

@Service
public class SteamOpenIDService {

    private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login";
    private static final String REALM = "http://localhost:8080";
    private static final String RETURN_TO = "http://localhost:8080/myapp/steam-callback";

    public static URI getAuthenticationRequestURI() throws URISyntaxException {
        Map<String, String> params = new HashMap<>();
        params.put("openid.ns", "http://specs.openid.net/auth/2.0");
        params.put("openid.mode", "checkid_setup");
        params.put("openid.return_to", RETURN_TO);
        params.put("openid.realm", REALM);
        params.put("openid.identity", "http://specs.openid.net/auth/2.0/identifier_select");
        params.put("openid.claimed_id", "http://specs.openid.net/auth/2.0/identifier_select");

        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            query.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        query.deleteCharAt(query.length() - 1);

        return new URI(STEAM_OPENID_URL + "?" + query.toString());
    }
}