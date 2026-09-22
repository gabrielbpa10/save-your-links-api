package com.save.link.api.dto;

import java.time.LocalDateTime;

public record ApiErrorDTO (
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message
) {

}
