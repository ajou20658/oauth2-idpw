package com.login.login.jwt.config;

import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import com.login.login.jwt.JwtTokenProvider;
import com.login.login.jwt.TokenDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final RedisTemplate<String,Object> redisTemplate;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = tokenProvider.resolveAccessToken(request);

        if(accessToken != null && tokenProvider.validateToken(accessToken)){
            if(isTokenBlacklisted(accessToken)){
                throw new CustomException(ControllerMessage.INVALID_TOKEN);
            }

            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
    private boolean isTokenBlacklisted(String token){
        return redisTemplate.opsForValue().get(token) != null;
    }
}
