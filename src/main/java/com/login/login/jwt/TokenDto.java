package com.login.login.jwt;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}
