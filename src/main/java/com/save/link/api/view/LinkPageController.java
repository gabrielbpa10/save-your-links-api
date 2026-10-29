package com.save.link.api.view;

import com.save.link.api.model.dto.LinkDTO;
import com.save.link.api.model.dto.form.LinkForm;
import com.save.link.api.service.LinkService;
import com.save.link.api.util.exception.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/links/cadastrar")
    public String salvarLink(
            @RequestParam String descTitle,
            @RequestParam String descURL,
            @RequestParam(defaultValue = "gabrielaraujoprofissionalti@gmail.com") String emailUser,
            RedirectAttributes redirectAttributes
    ) {

        try {
            LinkForm form = new LinkForm();
            form.descTitle = descTitle;
            form.descURL = descURL;
            form.emailUser = emailUser;

            linkService.save(form);
        } catch (BusinessException businessException) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    businessException.getMessage()
            );

            return "redirect:/links/cadastrar";
        }

        redirectAttributes.addFlashAttribute(
                "mensagemSucesso",
                "Link cadastrado com sucesso."
        );

        return "redirect:/meus-links";
    }
}
