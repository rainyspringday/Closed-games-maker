package com.example.TestApp;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Value("${steam.openid.endpoint}")
    private String steamOpenIdEndpoint;

    private ConsumerManager manager;

    @GetMapping
    public RedirectView login(HttpSession session) throws Exception {
        List discoveries = manager.discover(steamOpenIdEndpoint);
        DiscoveryInformation discovered = manager.associate(discoveries);
        session.setAttribute("openid-disc", discovered);

        String returnUrl = "http://localhost:8080/login/verify";
        AuthRequest authReq = manager.authenticate(discovered, returnUrl);
        return new RedirectView(authReq.getDestinationUrl(true));
    }
    @GetMapping("/verify")
    public String verify(HttpServletRequest request, HttpSession session, @RequestParam Map<String, String> queryParams) throws Exception {
        ParameterList openidResp = new ParameterList(queryParams);

        DiscoveryInformation discovered = (DiscoveryInformation) session.getAttribute("openid-disc");
        VerificationResult verification = manager.verify(request.getRequestURL().toString(), openidResp, discovered);

        AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
        if (authSuccess != null) {
            String steamId = authSuccess.getIdentity();
            return "loginSuccess: " + steamId;
        }

        return "loginFailed";
    }

}
