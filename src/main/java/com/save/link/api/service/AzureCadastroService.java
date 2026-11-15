package com.save.link.api.service;

import com.save.link.api.model.dto.AzureCadastroResponseDTO;
import com.save.link.api.model.dto.form.UserForm;
import tools.jackson.databind.json.JsonMapper;
import java.util.Map;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class AzureCadastroService {

    private final RestClient azureRestClient;
    private final String clientId;

    public AzureCadastroService(
            @Qualifier("azureRestClient") RestClient azureRestClient,
            @Value("${azure.external-id.client-id}") String clientId
    ) {
        this.azureRestClient = azureRestClient;
        this.clientId = clientId;
    }

    public String iniciarCadastro(String email) {

        MultiValueMap<String, String> formulario =
                new LinkedMultiValueMap<>();

        formulario.add("client_id", clientId);
        formulario.add("username", email);
        formulario.add("challenge_type", "oob password redirect");

        AzureCadastroResponseDTO resposta = azureRestClient.post()
                .uri("/signup/v1.0/start")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario)
                .retrieve()
                .body(AzureCadastroResponseDTO.class);

        if (resposta == null) {
            throw new IllegalStateException("O Azure retornou uma resposta vazia.");
        }

        if ("redirect".equals(resposta.challengeType())) {
            throw new IllegalStateException(
                    "O Azure solicitou continuar a autenticação pelo navegador."
            );
        }

        if (resposta.continuationToken() == null
                || resposta.continuationToken().isBlank()) {
            throw new IllegalStateException(
                    "O Azure não retornou o token de continuação."
            );
        }

        return resposta.continuationToken();
    }

    public String enviarCodigo(String continuationToken) {

        MultiValueMap<String, String> formulario =
                new LinkedMultiValueMap<>();

        formulario.add("client_id", clientId);
        formulario.add("continuation_token", continuationToken);
        formulario.add("challenge_type", "oob password redirect");

        AzureCadastroResponseDTO resposta = azureRestClient.post()
                .uri("/signup/v1.0/challenge")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario)
                .retrieve()
                .body(AzureCadastroResponseDTO.class);

        if (resposta == null) {
            throw new IllegalStateException(
                    "O Azure retornou uma resposta vazia."
            );
        }

        if ("redirect".equals(resposta.challengeType())) {
            throw new IllegalStateException(
                    "O Azure solicitou continuar a autenticação pelo navegador."
            );
        }

        if (!"oob".equals(resposta.challengeType())) {
            throw new IllegalStateException(
                    "O Azure retornou uma etapa diferente da verificação por e-mail."
            );
        }

        if (resposta.continuationToken() == null
                || resposta.continuationToken().isBlank()) {
            throw new IllegalStateException(
                    "O Azure não retornou o token de continuação."
            );
        }

        return resposta.continuationToken();
    }

    public String validarCodigo(String continuationToken, String codigo) {

        MultiValueMap<String, String> formulario =
                new LinkedMultiValueMap<>();

        formulario.add("client_id", clientId);
        formulario.add("continuation_token", continuationToken);
        formulario.add("grant_type", "oob");
        formulario.add("oob", codigo.trim());

        AzureCadastroResponseDTO resposta;

        try {
            resposta = azureRestClient.post()
                    .uri("/signup/v1.0/continue")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formulario)
                    .retrieve()
                    .body(AzureCadastroResponseDTO.class);

        } catch (RestClientResponseException exception) {
            AzureCadastroResponseDTO erro = exception.getResponseBodyAs(
                    AzureCadastroResponseDTO.class
            );

            boolean precisaDeSenha =
                    exception.getStatusCode().value() == 400
                            && erro != null
                            && "credential_required".equals(erro.error());

            if (!precisaDeSenha) {
                throw exception;
            }

            resposta = erro;
        }

        if (resposta == null) {
            throw new IllegalStateException("Resposta vazia do Azure.");
        }

        if ("redirect".equals(resposta.challengeType())) {
            throw new IllegalStateException(
                    "O Azure solicitou continuar pelo navegador."
            );
        }

        if (resposta.continuationToken() == null
                || resposta.continuationToken().isBlank()) {
            throw new IllegalStateException(
                    "O Azure não retornou o token de continuação."
            );
        }

        return resposta.continuationToken();
    }

    public String prepararSenha(String continuationToken) {

        MultiValueMap<String, String> formulario =
                new LinkedMultiValueMap<>();

        formulario.add("client_id", clientId);
        formulario.add("continuation_token", continuationToken);
        formulario.add("challenge_type", "password redirect");

        AzureCadastroResponseDTO resposta = azureRestClient.post()
                .uri("/signup/v1.0/challenge")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario)
                .retrieve()
                .body(AzureCadastroResponseDTO.class);

        validarTokenResposta(resposta);

        if (!"password".equals(resposta.challengeType())) {
            throw new IllegalStateException(
                    "O Azure não retornou a etapa de definição de senha."
            );
        }

        return resposta.continuationToken();
    }

    public AzureCadastroResponseDTO definirSenha(
            String continuationToken,
            String senha
    ) {
        MultiValueMap<String, String> formulario =
                new LinkedMultiValueMap<>();

        formulario.add("client_id", clientId);
        formulario.add("continuation_token", continuationToken);
        formulario.add("grant_type", "password");
        formulario.add("password", senha);

        AzureCadastroResponseDTO resposta;

        try {
            resposta = azureRestClient.post()
                    .uri("/signup/v1.0/continue")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formulario)
                    .retrieve()
                    .body(AzureCadastroResponseDTO.class);

        } catch (RestClientResponseException exception) {
            AzureCadastroResponseDTO erro = exception.getResponseBodyAs(
                    AzureCadastroResponseDTO.class
            );

            boolean precisaDeAtributos =
                    exception.getStatusCode().value() == 400
                            && erro != null
                            && "attributes_required".equals(erro.error());

            if (!precisaDeAtributos) {
                throw exception;
            }

            resposta = erro;
        }

        validarTokenResposta(resposta);

        return resposta;
    }

    public AzureCadastroResponseDTO definirAtributos(String continuationToken, UserForm cadastro) {
        MultiValueMap<String, String> formulario = new LinkedMultiValueMap<>();
        formulario.add("client_id", clientId);
        formulario.add("continuation_token", continuationToken);
        formulario.add("grant_type", "attributes");
        formulario.add("attributes", JsonMapper.builder().build().writeValueAsString(Map.of(
                "displayName", cadastro.name() + " " + cadastro.lastName(),
                "givenName", cadastro.name(),
                "surname", cadastro.lastName()
        )));
        AzureCadastroResponseDTO resposta = azureRestClient.post()
                .uri("/signup/v1.0/continue")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario)
                .retrieve()
                .body(AzureCadastroResponseDTO.class);
        validarTokenResposta(resposta);
        return resposta;
    }

    public String obterIdUsuario(String continuationToken) {
        MultiValueMap<String, String> formulario = new LinkedMultiValueMap<>();
        formulario.add("client_id", clientId);
        formulario.add("continuation_token", continuationToken);
        formulario.add("grant_type", "continuation_token");
        formulario.add("scope", "openid profile email");
        var resposta = azureRestClient.post()
                .uri("/oauth2/v2.0/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario)
                .retrieve()
                .body(tools.jackson.databind.JsonNode.class);
        if (resposta == null || !resposta.path("id_token").isTextual()) {
            throw new IllegalStateException("O Azure não retornou o ID token do usuário.");
        }
        // O token é recebido diretamente do Azure por HTTPS, nunca do formulário.
        // A leitura abaixo identifica o cadastro; não autentica uma sessão de login.
        String[] partes = resposta.path("id_token").asText().split("\\.");
        if (partes.length != 3) {
            throw new IllegalStateException("O Azure retornou um ID token inválido.");
        }
        var claims = JsonMapper.builder().build().readTree(new String(
                Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8));
        String oid = claims.path("oid").asText("");
        if (oid.isBlank() || !clientId.equals(claims.path("aud").asText())) {
            throw new IllegalStateException("O ID token não contém a identificação esperada do usuário.");
        }
        return java.util.UUID.fromString(oid).toString();
    }

    private void validarTokenResposta(AzureCadastroResponseDTO resposta) {

        if (resposta == null) {
            throw new IllegalStateException(
                    "O Azure retornou uma resposta vazia."
            );
        }

        if ("redirect".equals(resposta.challengeType())) {
            throw new IllegalStateException(
                    "O Azure solicitou continuar pelo navegador."
            );
        }

        if (resposta.continuationToken() == null
                || resposta.continuationToken().isBlank()) {
            throw new IllegalStateException(
                    "O Azure não retornou o token de continuação."
            );
        }
    }
}