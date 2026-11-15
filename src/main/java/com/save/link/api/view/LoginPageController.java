package com.save.link.api.view;

import com.save.link.api.model.dto.form.UserForm;
import com.save.link.api.service.AzureCadastroService;
import com.save.link.api.service.UserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import com.save.link.api.model.dto.AzureCadastroResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginPageController {

    private final UserService userService;

    private final  AzureCadastroService azureCadastroService;

    public LoginPageController(UserService userService, AzureCadastroService azureCadastroService) {
        this.userService = userService;
        this.azureCadastroService = azureCadastroService;
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

    @GetMapping("/signup-valida-email")
    public String paginaValidarEmail() {
        return "pages/login/singup-valida-email";
    }

    @GetMapping("/signup-valida-password")
    public String paginaValidarSenha() {
        return "pages/login/singup-valida-password";
    }

    @PostMapping("/enviar-codigo")
    public String validarEmail(
            @RequestParam String azureCadastroToken,
            @RequestParam String codigo,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("azureCadastroToken", azureCadastroToken);

        if (azureCadastroToken.isBlank()) {
            model.addAttribute("mensagemErro", "Sua sessão de cadastro expirou. Inicie o cadastro novamente.");
            return "pages/login/singup-valida-email";
        }

        if (!codigo.matches("[0-9]+")) {
            model.addAttribute("mensagemErro", "O código informado está errado. Digite apenas os números recebidos por e-mail.");
            return "pages/login/singup-valida-email";
        }

        try {
            String tokenSenha = azureCadastroService.validarCodigo(azureCadastroToken, codigo);
            redirectAttributes.addFlashAttribute("azureCadastroToken", tokenSenha);
            return "redirect:/signup-valida-password";
        } catch (RestClientResponseException exception) {
            model.addAttribute("mensagemErro", exception.getStatusCode().value() == 400
                    ? "O código informado está errado ou expirou. Confira o código recebido e tente novamente."
                    : "Não foi possível validar o código agora. Tente novamente.");
        } catch (Exception exception) {
            model.addAttribute("mensagemErro", "Não foi possível validar o código agora. Tente novamente.");
        }

        return "pages/login/singup-valida-email";
    }

    @PostMapping("/signup-valida-password")
    public String definirSenha(
            @RequestParam(defaultValue = "") String azureCadastroToken,
            @RequestParam(defaultValue = "") String password,
            @RequestParam(defaultValue = "") String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
        model.addAttribute("azureCadastroToken", azureCadastroToken);
        if (azureCadastroToken.isBlank()) {
            model.addAttribute("mensagemErro", "Sua sessão de cadastro expirou. Inicie o cadastro novamente.");
            return "pages/login/singup-valida-password";
        }
        if (password.isBlank() || password.length() < 8 || !password.equals(confirmPassword)) {
            model.addAttribute("mensagemErro", "As senhas devem ser iguais e ter pelo menos 8 caracteres.");
            return "pages/login/singup-valida-password";
        }

        try {
            UserForm usuarioCadastro = (UserForm) session.getAttribute("azureCadastroUsuario");
            if (usuarioCadastro == null) {
                throw new IllegalStateException("Dados do cadastro indisponíveis.");
            }
            String tokenConcluido = (String) session.getAttribute("azureCadastroConcluidoToken");
            String tokenAtributos = (String) session.getAttribute("azureAtributosToken");
            AzureCadastroResponseDTO resposta;
            if (azureCadastroToken.equals(tokenConcluido)) {
                resposta = new AzureCadastroResponseDTO(tokenConcluido, null, null, null);
            } else if (azureCadastroToken.equals(tokenAtributos)) {
                resposta = new AzureCadastroResponseDTO(tokenAtributos, null, "attributes_required", null);
            } else {
                String tokenPreparado = (String) session.getAttribute("azureSenhaToken");
                if (!azureCadastroToken.equals(tokenPreparado)) {
                    tokenPreparado = azureCadastroService.prepararSenha(azureCadastroToken);
                    session.setAttribute("azureSenhaToken", tokenPreparado);
                }
                model.addAttribute("azureCadastroToken", tokenPreparado);
                resposta = azureCadastroService.definirSenha(tokenPreparado, password);
            }
            model.addAttribute("azureCadastroToken", resposta.continuationToken());
            if ("attributes_required".equals(resposta.error())) {
                session.setAttribute("azureAtributosToken", resposta.continuationToken());
                UserForm cadastro = (UserForm) session.getAttribute("azureCadastroUsuario");
                if (cadastro == null) {
                    throw new IllegalStateException("Dados do cadastro indisponíveis.");
                }
                resposta = azureCadastroService.definirAtributos(resposta.continuationToken(), cadastro);
            }
            if (resposta.error() != null && !resposta.error().isBlank()) {
                throw new IllegalStateException("O Azure não concluiu o cadastro.");
            }
            session.setAttribute("azureCadastroConcluidoToken", resposta.continuationToken());
            model.addAttribute("azureCadastroToken", resposta.continuationToken());
            String idAzure = (String) session.getAttribute("azureCadastroIdUsuario");
            if (idAzure == null) {
                idAzure = azureCadastroService.obterIdUsuario(resposta.continuationToken());
                session.setAttribute("azureCadastroIdUsuario", idAzure);
            }
            userService.atualizarIdEntreAzure(usuarioCadastro.email(), idAzure);
            session.removeAttribute("azureCadastroIdUsuario");
            session.removeAttribute("azureCadastroConcluidoToken");
            session.removeAttribute("azureSenhaToken");
            session.removeAttribute("azureCadastroUsuario");
            session.removeAttribute("azureAtributosToken");
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Conta criada com sucesso. Entre para acessar seus links!");
            return "redirect:/login";
        } catch (RestClientResponseException exception) {
            model.addAttribute("mensagemErro", exception.getStatusCode().value() == 400
                    ? "Não foi possível definir a senha. Confira os requisitos de senha do Azure e tente novamente."
                    : "Não foi possível concluir o cadastro agora. Tente novamente.");
        } catch (Exception exception) {
            model.addAttribute("mensagemErro", "Não foi possível concluir o cadastro. Tente novamente.");
        }
        return "pages/login/singup-valida-password";
    }

    @PostMapping("/cadastrar-novo-usuario")
    public String cadastrarNovoUsuarioEnviarCodigoEmail(
            @Valid @ModelAttribute UserForm userForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session
            ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    bindingResult.getAllErrors().getFirst().getDefaultMessage()
            );
            return "redirect:/signup";
        }

        try {
            userService.hasEmailCadastrado(userForm.email().trim());
            String tokenInicial = azureCadastroService.iniciarCadastro(userForm.email().trim());
            String tokenVerificacao = azureCadastroService.enviarCodigo(tokenInicial);
            userService.save(userForm);
            session.setAttribute("azureCadastroUsuario", userForm);
            session.removeAttribute("azureCadastroIdUsuario");
            session.removeAttribute("azureCadastroConcluidoToken");
            session.removeAttribute("azureAtributosToken");
            session.removeAttribute("azureSenhaToken");

            redirectAttributes.addFlashAttribute(
                    "azureCadastroToken",
                    tokenVerificacao
            );
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    exception.getMessage());
            return "redirect:/signup";
        }

        return "redirect:/signup-valida-email";
    }
}
