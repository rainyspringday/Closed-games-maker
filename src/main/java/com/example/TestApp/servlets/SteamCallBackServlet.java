package com.example.TestApp.servlets;

import com.example.TestApp.security.JWTcore;
import com.example.TestApp.MyUserDetails;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.Cookie;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.io.entity.StringEntity;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@WebServlet("/myapp/steam-callback")
public class SteamCallBackServlet extends HttpServlet {
    @Autowired
    private static final String API_URL = "https://api.opendota.com/api/players/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue().length > 0) {
                params.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        if (verifySteamOpenID(params)) {
            String steamID = extractSteamID(params.get("openid.claimed_id"));
            String steamID32 = convertSteamID(steamID).toString();
            try {
                String[] profile = getDotaProfile(steamID32);
                String token=authenticateUser();

                Cookie tokenCookie = new Cookie("token", token);
                tokenCookie.setHttpOnly(true);
                tokenCookie.setSecure(true);
                tokenCookie.setPath("/");
                tokenCookie.setMaxAge(60 * 60);
                response.addCookie(tokenCookie);
                response.sendRedirect("/myapp/secure-endpoint");

            } catch (Exception e) {
                System.err.println("An error occurred while fetching the Dota profile: " + e.getMessage());
            }
        } else {
            response.getWriter().println("OpenID verification failed");
        }
    }

    private boolean verifySteamOpenID(Map<String, String> params) throws IOException {
        try {
            URI uri = new URIBuilder("https://steamcommunity.com/openid/login")
                    .addParameter("openid.ns", "http://specs.openid.net/auth/2.0")
                    .addParameter("openid.mode", "check_authentication")
                    .addParameter("openid.signed", params.get("openid.signed"))
                    .addParameter("openid.sig", params.get("openid.sig"))
                    .addParameter("openid.assoc_handle", params.get("openid.assoc_handle"))
                    .addParameter("openid.claimed_id", params.get("openid.claimed_id"))
                    .addParameter("openid.identity", params.get("openid.identity"))
                    .addParameter("openid.return_to", params.get("openid.return_to"))
                    .addParameter("openid.response_nonce", params.get("openid.response_nonce"))
                    .addParameter("openid.op_endpoint", params.get("openid.op_endpoint"))
                    .build();
            HttpPost httpPost=new HttpPost(uri);
            StringEntity entity = new StringEntity("");
            httpPost.setEntity(entity);
            try (var httpClient = HttpClients.createDefault()) {

                ClassicHttpResponse httpResponse = httpClient.execute(httpPost);
                int statusCode = httpResponse.getCode();
                if (statusCode >= 200 && statusCode < 300) {
                    String responseString=new BasicHttpClientResponseHandler().handleResponse( httpResponse);
                    if (responseString != null) {
                        return responseString.contains("is_valid:true");
                    }
                } else {
                    throw new HttpResponseException(statusCode, httpResponse.getReasonPhrase());
                }
                return false;
            }

        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI for OpenID verification", e);
        }
    }


    public static String[] getDotaProfile(String steamId) throws Exception {
        try (var httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + steamId);

            try (ClassicHttpResponse httpResponse = httpClient.execute(httpGet)) {
                String responseString = new BasicHttpClientResponseHandler().handleResponse(httpResponse);
                if (responseString != null) {
                    try (JsonReader jsonReader = Json.createReader(new StringReader(responseString))) {
                        JsonObject jsonObject = jsonReader.readObject();
                        JsonObject profile = jsonObject.getJsonObject("profile");
                        String name = profile != null && profile.containsKey("personaname") ? profile.getString("personaname") : "N/A";
                        String profilePicture = profile != null && profile.containsKey("avatarfull") ? profile.getString("avatarfull") : "N/A";
                        return new String[]{name, profilePicture};
                    }
                    catch(JsonException e)
                    {
                        System.err.println("JSON failed");
                    }
                }
            }
            catch(HttpResponseException e)
            {
                System.err.println("Get failed");
            }
            return new String[]{"N/A", "N/A"};
        }
        catch (Exception e){
            throw new Exception("Client failed", e);
        }
    }


    private String extractSteamID(String claimedId) {
        String prefix = "https://steamcommunity.com/openid/id/";
        return claimedId.startsWith(prefix) ? claimedId.substring(prefix.length()) : null;
    }

    private Integer convertSteamID(String steamID)
    {
        try {
            String binaryString = Long.toBinaryString(Long.parseLong(steamID));
            String lower32Bits = binaryString.substring(Math.max(0, binaryString.length() - 32));
            return Integer.parseUnsignedInt(lower32Bits, 2);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid SteamID64 format: " + steamID, e);
        }
    }

    private String authenticateUser()
    {
        SimpleGrantedAuthority roleUserAuthority = new SimpleGrantedAuthority("ROLE_USER");
        List<SimpleGrantedAuthority> updatedAuthorities = Collections.singletonList(roleUserAuthority);
        MyUserDetails userDetails = new MyUserDetails("test", "test", updatedAuthorities);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                updatedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        JWTcore jwTcore= new JWTcore();
        String token=jwTcore.generateToken(newAuth);
        return token;
    }


}