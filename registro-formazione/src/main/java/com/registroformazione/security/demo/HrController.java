package com.registroformazione.security.demo;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hr")
@PreAuthorize("hasRole('ROLE_HR')")
@SecurityRequirement(name = "bearerAuth")
public class HrController {

    @GetMapping
    @PreAuthorize("hasAuthority('hr:read')")
    public String get() {
        return "GET:: hr controller";
    }
    @PostMapping
    @PreAuthorize("hasAuthority('hr:create')")
    @Hidden
    public String post() {
        return "POST:: hr controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority('hr:update')")
    @Hidden
    public String put() {
        return "PUT:: hr controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('hr:delete')")
    @Hidden
    public String delete() {
        return "DELETE:: hr controller";
    }
}
