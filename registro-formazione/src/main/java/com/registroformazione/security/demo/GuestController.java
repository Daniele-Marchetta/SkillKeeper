package com.registroformazione.security.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
@Tag(name = "guest")
public class GuestController {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Get endpoint for manager",
            summary = "This is a summary for management get endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @GetMapping
    @PreAuthorize("hasAuthority('guest:read')")
    public String get() {
        return "GET:: guest controller";
    }
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasAuthority('guest:create')")
    public String post() {
        return "POST:: guest controller";
    }
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    @PreAuthorize("hasAuthority('guest:update')")
    public String put() {
        return "PUT:: guest controller";
    }
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    @PreAuthorize("hasAuthority('guest:delete')")
    public String delete() {
        return "DELETE:: guest controller";
    }
}
