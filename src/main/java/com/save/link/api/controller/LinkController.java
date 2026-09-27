package com.save.link.api.controller;

import com.save.link.api.dto.form.LinkForm;
import com.save.link.api.service.LinkService;
import com.save.link.api.util.response.CustomResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @PostMapping
    public ResponseEntity<CustomResponse> saveNewProduct(@Valid @RequestBody LinkForm linkForm) {
        linkService.save(linkForm);
        return ResponseEntity.ok(new CustomResponse(null, 201, "Link salvo com sucesso!"));
    }

    @GetMapping("/find-by-url/{filter}")
    public ResponseEntity<CustomResponse> findByURL(@PathVariable String filter) {
        return ResponseEntity.ok(new CustomResponse(this.linkService.findByURL(filter), 200, ""));
    }
}
