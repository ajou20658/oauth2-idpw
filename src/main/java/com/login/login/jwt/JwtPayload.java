package com.login.login.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor
public class JwtPayload {
    private Long id;
    private Date issuedAt;
    public JwtPayload(Long id, Date issuedAt){
        this.id = id;
        this.issuedAt = issuedAt;
    }
}
