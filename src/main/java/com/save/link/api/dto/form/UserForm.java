package com.save.link.api.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserForm(
        @NotBlank(message = "Nome é obrigatório.")
        String name,

        @NotBlank(message = "Sobrenome é obrigatório.")
        String lastName,

        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail iválido.")
        String email
) {
}
