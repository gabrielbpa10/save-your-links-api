package com.save.link.api.view;

import com.save.link.api.service.LinkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LinkPageController {

    private final LinkService linkService;

    public LinkPageController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/meus-links")
    public String listar(
            @RequestParam(defaultValue = "") String pesquisa,
            Model model) {
        model.addAttribute("titulo", "Save Your Links");
        model.addAttribute("pesquisa", pesquisa);
        model.addAttribute("links",
                pesquisa.isBlank() ? List.of() : linkService.findByURL(pesquisa)
                );

        return "pages/links/buscador";
    }

    @GetMapping("/links/cadastrar")
    public String cadastrarNovoLink() {
        return "pages/links/cadastro-link";
    }
}
