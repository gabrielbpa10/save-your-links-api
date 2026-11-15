package com.save.link.api.view;

import com.save.link.api.service.AzureCadastroService;
import com.save.link.api.service.UserService;
import com.save.link.api.model.dto.AzureCadastroResponseDTO;
import com.save.link.api.model.dto.form.UserForm;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginPageControllerTest {
    private final AzureCadastroService azure = mock(AzureCadastroService.class);
    private final UserService users = mock(UserService.class);
    private final LoginPageController controller = new LoginPageController(users, azure);
    private final ExtendedModelMap model = new ExtendedModelMap();
    private final RedirectAttributesModelMap flash = new RedirectAttributesModelMap();
    private final MockHttpSession session = new MockHttpSession();

    @org.junit.jupiter.api.BeforeEach
    void prepareSession() {
        session.setAttribute("azureCadastroUsuario", new UserForm("Ana", "Silva", "ana@example.com"));
        when(azure.obterIdUsuario("done")).thenReturn("11111111-2222-3333-4444-555555555555");
    }

    @Test
    void completesSignupInOrderAndRedirectsToLogin() {
        when(azure.prepararSenha("email-token")).thenReturn("password-token");
        when(azure.definirSenha("password-token", "Senha123!"))
                .thenReturn(new AzureCadastroResponseDTO("done", null, null, null));
        assertEquals("redirect:/login", submit("email-token", "Senha123!", "Senha123!"));
        var order = inOrder(azure);
        order.verify(azure).prepararSenha("email-token");
        order.verify(azure).definirSenha("password-token", "Senha123!");
        assertNotNull(flash.getFlashAttributes().get("mensagemSucesso"));
        assertNull(session.getAttribute("azureSenhaToken"));
        verify(users).atualizarIdEntreAzure("ana@example.com", "11111111-2222-3333-4444-555555555555");
    }

    @Test
    void rejectsDifferentPasswordsBeforeCallingAzure() {
        assertEquals("pages/login/singup-valida-password", submit("token", "Senha123!", "Outra123!"));
        verifyNoInteractions(azure);
        assertNotNull(model.get("mensagemErro"));
    }

    @Test
    void preservesPreparedTokenOnFailureAndReusesItOnRetry() {
        when(azure.prepararSenha("email-token")).thenReturn("password-token");
        when(azure.definirSenha("password-token", "Senha123!")).thenThrow(new IllegalStateException());
        assertEquals("pages/login/singup-valida-password", submit("email-token", "Senha123!", "Senha123!"));
        assertEquals("password-token", model.get("azureCadastroToken"));
        submit("password-token", "Senha123!", "Senha123!");
        verify(azure, times(1)).prepararSenha(anyString());
        assertTrue(flash.getFlashAttributes().isEmpty());
    }

    @Test
    void submitsRequiredAttributesBeforeReportingSuccess() {
        UserForm user = new UserForm("Ana", "Silva", "ana@example.com");
        session.setAttribute("azureCadastroUsuario", user);
        when(azure.prepararSenha("token")).thenReturn("prepared");
        when(azure.definirSenha("prepared", "Senha123!"))
                .thenReturn(new AzureCadastroResponseDTO("attributes-token", null, "attributes_required", null));
        when(azure.definirAtributos("attributes-token", user))
                .thenReturn(new AzureCadastroResponseDTO("done", null, null, null));
        assertEquals("redirect:/login", submit("token", "Senha123!", "Senha123!"));
        verify(azure).definirAtributos("attributes-token", user);
        assertNull(session.getAttribute("azureCadastroUsuario"));
    }

    @Test
    void retriesDatabaseUpdateWithoutResubmittingPasswordOrExchangingToken() {
        when(azure.prepararSenha("token")).thenReturn("prepared");
        when(azure.definirSenha("prepared", "Senha123!"))
                .thenReturn(new AzureCadastroResponseDTO("done", null, null, null));
        doThrow(new IllegalStateException()).doNothing().when(users)
                .atualizarIdEntreAzure(anyString(), anyString());
        assertEquals("pages/login/singup-valida-password", submit("token", "Senha123!", "Senha123!"));
        assertTrue(flash.getFlashAttributes().isEmpty());
        assertEquals("done", model.get("azureCadastroToken"));
        assertEquals("redirect:/login", submit("done", "Senha123!", "Senha123!"));
        verify(azure, times(1)).definirSenha(anyString(), anyString());
        verify(azure, times(1)).obterIdUsuario("done");
    }

    private String submit(String token, String password, String confirmation) {
        return controller.definirSenha(token, password, confirmation, model, flash, session);
    }
}
