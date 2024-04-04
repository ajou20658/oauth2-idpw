package com.login.login.member.service.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class SignupResponse {
    private String email;
    private String name;
}
