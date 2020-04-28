package com.example.client_demo;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProductController {

    RestTemplate restTemplate;

    private final String base_url = "http://127.0.0.1:8080";

    public ProductController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        String url = base_url + "/hello";
        String response = restTemplate.getForObject(url, String.class);
        model.addAttribute("message", response);
        return "hello";
    }

    @GetMapping(path = "/products")
    public String getProducts(Model model){
        String url = base_url + "/list";
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        List<String> products = response.getBody();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping(path = "userinfo")
    public String userInfo(HttpServletRequest request, Model model){
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal=(KeycloakPrincipal)token.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        UserInfo info = new UserInfo();
        info.username = accessToken.getPreferredUsername();
        info.emailID = accessToken.getEmail();
        info.lastname = accessToken.getFamilyName();
        info.firstname = accessToken.getGivenName();
        info.realmName = accessToken.getIssuer();
        AccessToken.Access realmAccess = accessToken.getRealmAccess();
        info.roles = realmAccess.getRoles().toString();
        info.scopes = accessToken.getScope();
        info.accessToken = session.getTokenString();
        info.idToken = session.getIdTokenString();
        model.addAttribute("userinfo", info);
        return "userinfo";
    }

    @GetMapping(path = "/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }

    public static class UserInfo {
        public String username;
        public String emailID;
        public String lastname;
        public String firstname;
        public String realmName;
        public String roles;
        public String scopes;
        public String accessToken;
        public String idToken;

    }

}