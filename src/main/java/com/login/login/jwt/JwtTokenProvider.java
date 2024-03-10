package com.login.login.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${JWT_REFRESH}")
    private Long refresh;
    public static final String BEARER_TYPE = "Bearer";
    public static final String AUTHORITIES_KEY="auth";
    private final SecretKey secretKey;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey){
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }
    public TokenDto generateToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + expiration);
        String accessToken = Jwts.builder().subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities).expiration(accessTokenExpiresIn)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        String refreshToken = Jwts.builder().expiration(new Date(now + refresh))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }
    public Authentication getAuthentication(String token){
        Claims claims = parse(token);
        if(claims.get(AUTHORITIES_KEY) == null){
            throw new RuntimeException("No Auth Token");
        }
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }
    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token", e);
        }catch (ExpiredJwtException e){
            throw new ExpiredJwtException(null, null, "Expired JWT Token");
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token", e);
        }catch (IllegalArgumentException e){
            log.info("JWT claims string is empty", e);
        }
        return false;
    }
    private Claims parse(String token){
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build().parseSignedClaims(token).getPayload();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
