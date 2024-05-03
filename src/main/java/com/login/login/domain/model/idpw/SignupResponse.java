package com.login.login.domain.model.idpw;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class SignupResponse {
    private String email;
    private String name;
}
