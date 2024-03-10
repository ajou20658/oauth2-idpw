package com.login.login.oauth2.service.factory;

import com.login.login.jwt.JwtTokenProvider;
import com.login.login.jwt.TokenDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
        log.info("authentication = {}",authentication);
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        if(authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken){
            TokenDto token = jwtTokenProvider.generateToken(authentication);
            log.info("token : {}", token);
            response.addHeader("Authorization", "Bearer " + token.getAccessToken());
            response.addHeader("RefreshToken", "Bearer " + token.getRefreshToken());
        }
        setDefaultTargetUrl("/hello");

    }
}
