package com.order.manager.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleController {
    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        System.out.println(appName);
        return "home";
    }

    @GetMapping("/home/htmllearning")
    public String learnHtml(Model model) {
        model.addAttribute("appName", appName);
        System.out.println("hehehheheheeheheheheh");
        return "Htmllearning";
    }
}

