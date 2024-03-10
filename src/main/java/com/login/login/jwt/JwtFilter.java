package com.login.login.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshToken";
    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);
        try{
            if(accessToken != null && tokenProvider.validateToken(accessToken)){
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (ExpiredJwtException e){
            if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
                Authentication authentication = tokenProvider.getAuthentication(refreshToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                TokenDto tokenDto = tokenProvider.generateToken(authentication);
                String newAccessToken = tokenDto.getAccessToken();
                String newRefreshToken = tokenDto.getRefreshToken();

                response.setHeader(AUTHORIZATION_HEADER, newAccessToken);
                response.setHeader(REFRESH_HEADER, newRefreshToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    private String resolveAccessToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }
    private String resolveRefreshToken(HttpServletRequest request){
        String bearerToken = request.getHeader(REFRESH_HEADER);
        if(bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }
}
