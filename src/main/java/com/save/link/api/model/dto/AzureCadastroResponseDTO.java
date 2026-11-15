package com.save.link.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AzureCadastroResponseDTO(
        @JsonProperty("continuation_token")
        String continuationToken,

        @JsonProperty("challenge_type")
        String challengeType,

        String error,

        String suberror
) {
}
