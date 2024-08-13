package com.example.TestApp.servlets;

import com.example.TestApp.services.SteamOpenIDService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@WebServlet("/myapp/steam-login")
public class SteamLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            URI authUri = SteamOpenIDService.getAuthenticationRequestURI();
            response.sendRedirect(authUri.toString());
        } catch (URISyntaxException e) {
            throw new ServletException("Failed to create authentication request URI", e);
        }

    }
}