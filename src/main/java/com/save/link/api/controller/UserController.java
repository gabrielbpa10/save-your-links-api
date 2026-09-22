package com.save.link.api.controller;

import com.save.link.api.dto.form.UserForm;
import com.save.link.api.service.UserService;
import com.save.link.api.util.response.CustomResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<CustomResponse> cadastrarNovoProduto(@Valid @RequestBody UserForm userForm) {
        userService.save(userForm);
        return ResponseEntity.ok(new CustomResponse(null, 201, "Produto cadastrado com sucesso!"));
    }
}
