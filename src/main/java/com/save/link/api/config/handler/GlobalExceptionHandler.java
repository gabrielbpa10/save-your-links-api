package com.save.link.api.config.handler;

import com.save.link.api.dto.ApiErrorDTO;
import com.save.link.api.util.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDTO> handleBusiness(BusinessException businessException) {
        ApiErrorDTO apiError = new ApiErrorDTO(
                LocalDateTime.now(),
                400,
                "BUSINESS_ERROR",
                businessException.getMessage()
        );

        return ResponseEntity.badRequest().body(apiError);
    }
}
