package com.login.login.service.jwt;

import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final RedisTemplate<String,Object> redisTemplate;
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

//        if(accessToken != null && tokenProvider.validateToken(accessToken)){
//            if(isTokenBlacklisted(accessToken)){
//
//                throw new CustomException(ControllerMessage.INVALID_TOKEN);
////                filterChain.doFilter(request,response);
//            }
//
//            Authentication authentication = tokenProvider.getAuthentication(accessToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
        try{
            if(accessToken != null && tokenProvider.validateToken(accessToken)){
                if(isTokenBlacklisted(accessToken)){
                    throw new CustomException(ControllerMessage.INVALID_TOKEN);
                }
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception ex){
            SecurityContextHolder.clearContext();
        }
        log.info("jwt 유효하지 않음");
        filterChain.doFilter(request, response);
    }
    private boolean isTokenBlacklisted(String token){
        return redisTemplate.opsForValue().get(token) != null;
    }
    private boolean isOAuth2LoginPath(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/login");
    }
}

