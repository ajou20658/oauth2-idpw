package com.login.login.domain.model.idpw;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String role_key;
}
