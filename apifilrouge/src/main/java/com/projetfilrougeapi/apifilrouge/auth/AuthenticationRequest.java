package com.projetfilrougeapi.apifilrouge.auth;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String email;
    private String password;
}
