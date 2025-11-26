package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String username;
    private String email;
    private BigDecimal balance;
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse(Long id, String username, String email, BigDecimal balance, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.token = token;
    }
}
