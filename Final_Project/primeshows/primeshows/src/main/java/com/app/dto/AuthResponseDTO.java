package com.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
public class AuthResponseDTO {
    private final String accessToken;
    private String role;
    private final String tokenType = "Bearer"; // Fixed value
    public String getRole() { return getRole(); }

    public AuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
        
    }
    public AuthResponseDTO(String accessToken, String role) {
        this.accessToken = accessToken;
        this.role = role;
    }
    
    
}
