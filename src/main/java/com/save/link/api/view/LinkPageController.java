package com.save.link.api.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LinkPageController {

    @GetMapping("/meus-links")
    public String listar(Model model) {
        model.addAttribute("titulo", "Save Your Links");

//        model.addAttribute("links", List.of(
//                "https://spring.io",
//                "https://www.thymeleaf.org"
//        ));

        return "links/buscador";
    }
}
