package com.login.login.domain.service.idpw;

import com.login.login.infrastructure.entity.member.Member;
import com.login.login.infrastructure.entity.member.MemberRepository;
import com.login.login.domain.model.idpw.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomIdPwLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RestTemplate restTemplate;
    @Value("${jwt.url}")
    private String jwtUrl;
    @Value("jwt.redirect_url")
    private String redirectUrl;
    @Value("${jwt.issue}")
    private String issue;
    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info(String.valueOf(customUserDetails.getId())); // -> id 반환됨
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String jwtGenerateUrl = jwtUrl+issue+"?userId="+customUserDetails.getId()+"&userName="+customUserDetails.getName()+"&authorities="+authorities;
        Map<String,Object> jwt = restTemplate.getForObject(jwtGenerateUrl, JSONObject.class);
        log.info(jwt.toString());
        String access = jwt.get("accessToken").toString();
        String refresh = jwt.get("refreshToken").toString();
        Member member = memberRepository.findById(customUserDetails.getId()).get();
        member.updateRefreshToken(refresh);
        memberRepository.save(member);
//        request.getSession().setAttribute("access",access);
//        request.getSession().setAttribute("refresh", refresh);
//        response.sendRedirect(request.getContextPath()+"/api/jwt");//스프링 로그인 페이지 사용할 경우
        response.addHeader("access",access);
        response.addHeader("refresh",refresh);
        response.sendRedirect(redirectUrl+"/redirect?access="+access+"&refresh="+refresh); //프론트로 결과를 리다이렉션
    }
}
