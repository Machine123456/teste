package com.cap.authenticationservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class ModelController {
    
    @Value("${api.endpoint.product-service.adress}")
    private String productServiceUrl;
    
    @GetMapping("/home")
    public ModelAndView gotoHomePage(Model model){ //, HttpServletResponse response) {
        //response.setHeader("Authorization", "Bearer " + TokenService.SESSION_TOKEN);
        model.addAttribute("productServiceUrl", productServiceUrl);
        return new ModelAndView("home");
    }


    @GetMapping("/login")
    public ModelAndView gotoLoginPage(Model model) {
        model.addAttribute("productServiceUrl", productServiceUrl);
        return new ModelAndView("login");
    }

    @GetMapping("/admin")
    public ModelAndView gotoAdminPage(Model model) { 
        model.addAttribute("productServiceUrl", productServiceUrl);
        return new ModelAndView("admin");
    }


}
