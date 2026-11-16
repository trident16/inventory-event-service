package com.tarunnalla.inventory.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,30}$", message = "must be 3-30 letters, digits, dots, dashes or underscores")
        String username,

        @NotBlank
        @Size(min = 8, max = 72, message = "must be 8-72 characters")
        String password
) {
}
