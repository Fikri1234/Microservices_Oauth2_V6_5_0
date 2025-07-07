package com.project.client.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by user on 6:04 31/05/2025, 2025
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, OAuth2AuthenticationToken authentication) {
        model.addAttribute("username", authentication.getName());
        return "home"; // render home.html from src/main/resources/templates/
    }
}
