package com.login.login.service.jwt;

import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import com.login.login.member.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;

@Service
@Slf4j
public class JwtTokenProvider {
    private final RedisTemplate<String,Object> redisTemplate;
    private final MemberRepository memberRepository;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORITIES_KEY="auth";
    public static final String USER_ID = "sub";
    public static final String USER_NAME = "name";
    private final SecretKey secretKey;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, MemberRepository memberRepository, RedisTemplate<String,Object> redisTemplate){
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
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
            throw new CustomException(ControllerMessage.EXPIRED_ACCESSTOKEN);
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

    private long getMemberIdFromToken(String token){
        return Integer.valueOf(parse(token).getSubject());
    }
    public String resolveAccessToken(HttpServletRequest request){
        if(!StringUtils.hasText(AUTHORIZATION_HEADER)){
            return null;
        }
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.info("bearerToken : {}", bearerToken);
        if(bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }

}
