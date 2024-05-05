package com.login.login.domain.model.jwt;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtAttributes {
    private Long userId;
    private String access;
    private String refresh;
}
