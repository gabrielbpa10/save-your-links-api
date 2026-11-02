package com.save.link.api.view;

import com.save.link.api.model.dto.form.UserForm;
import com.save.link.api.service.UserService;
import com.save.link.api.util.exception.BusinessException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginPageController {

    private final UserService userService;

    public LoginPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/login", "/"})
    public String login(
            Model model
    ) {
        model.addAttribute("titulo", "Login | Save Your Links");
        return "pages/login/login";
    }

    @GetMapping("/signup")
    public String cadastro(Model model) {
        model.addAttribute("titulo", "Sign Up | Save Your Links");
        return "pages/login/signup";
    }

    @PostMapping("/cadastrar-novo-usuario")
    public String cadastrarNovoUsuario(
            @Valid @ModelAttribute UserForm userForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
            ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    bindingResult.getAllErrors().getFirst().getDefaultMessage()
            );
            return "redirect:/signup";
        }

        try {
            userService.save(userForm);
        } catch (BusinessException businessException) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    businessException.getMessage());
            return "redirect:/signup";
        }

        redirectAttributes.addFlashAttribute(
                "mensagemSucesso",
                "Conta cadastrada com sucesso. Seja bem-vindo(a)!"
        );

        return "redirect:/login";
    }

}
