package com.milhim.ecommerce.ecommerce.api.model;

import jakarta.validation.constraints.*;

public class LoginBody {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    private String username;

    public @NotNull @NotBlank @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$") String getPassword() {
        return password;
    }

    public @NotNull @NotBlank @Size(min = 3, max = 255) String getUsername() {
        return username;
    }

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    private String password;
}
