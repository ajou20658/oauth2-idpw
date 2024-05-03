package com.login.login.domain.service.jwt;

import com.login.login.common.exception.ControllerMessage;
import com.login.login.common.exception.CustomException;
import com.login.login.infrastructure.cache.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final RedisService redisService;
    private final JwtTokenProvider tokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException,CustomException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        log.info("requestURI : {}",requestURI);
        if(isOAuth2LoginPath(request)){
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = tokenProvider.resolveAccessToken(request);
        try{
            if(accessToken != null && tokenProvider.validateToken(accessToken)){
                if(redisService.isTokenBlacklisted(accessToken)){
                    log.info("로그아웃된 토큰");
                    throw new CustomException(ControllerMessage.INVALID_TOKEN);
                }
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                if(authentication==null){
                    log.info("authentication생성되지 않음");
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("jwt 유효함");
            }else{
                log.info("유효하지 않은 토큰");
            }
        }catch (Exception ex){
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
    private boolean isOAuth2LoginPath(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/login") || requestURI.startsWith("/api/pass");
    }
}

