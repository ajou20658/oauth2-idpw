package com.login.login.domain.service.oauth2;

import com.login.login.common.exception.ControllerMessage;
import com.login.login.common.exception.CustomException;
import com.login.login.domain.model.jwt.JwtAttributes;
import com.login.login.domain.service.jwt.JwtTokenProvider;
import com.login.login.infrastructure.entity.member.Member;
import com.login.login.infrastructure.entity.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOauthLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${jwt.redirect_url}")
    private String redirectUrl;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JwtAttributes jwt = tokenProvider.generateToken(authentication);
        String access = jwt.getAccessToken();
        String refresh = jwt.getRefreshToken();
        Member member = memberRepository.findById(jwt.getUserId()).orElseThrow(() -> new CustomException(ControllerMessage.INVALID_MEMBER));
        member.updateRefreshToken(refresh);
        memberRepository.save(member);
//        response.addHeader("access",access);
//        response.addHeader("refresh",refresh);
        response.sendRedirect(redirectUrl + "/redirect?access="+access+"&refresh="+refresh); //프론트로 결과를 리다이렉션
    }
}
