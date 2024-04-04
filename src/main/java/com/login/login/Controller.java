package com.login.login;

import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import com.login.login.member.entity.Member;
import com.login.login.member.service.MemberService;
import com.login.login.member.service.dto.SignupRequest;
import com.login.login.member.service.dto.SignupResponse;
import com.login.login.service.jwt.JwtTokenProvider;
import com.login.login.service.oauth2.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class Controller {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

//    @Deprecated(since = "테스트용")
//    @GetMapping("/api/user")
//    public OAuth2User getCurrentUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
//            return (OAuth2User) authentication.getPrincipal();
//        }else{
//            throw new CustomException(ControllerMessage.PLEASE_LOGIN);
//        }
//    }
    @GetMapping("/api/jwt")
    public ApiResponse showJwt(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        HttpSession  session = request.getSession();
        String access = (String) session.getAttribute("access");
        String refresh = (String) session.getAttribute("refresh");
        if(access == null || refresh == null){
            return new ApiResponse(ControllerMessage.RE_LOGIN.getHttpStatus(),ControllerMessage.RE_LOGIN.getMessage());
        }
        Map<String,String> jwt = new HashMap<>();
        jwt.put("access",access);
        jwt.put("refresh", refresh);
        response.addHeader("access",access);
        response.addHeader("refresh",refresh);
        response.sendRedirect("http://localhost:3000/");
        return new ApiResponse(HttpStatus.OK,jwt);
    }
    @PostMapping("/api/signup")
    public ApiResponse signup(HttpServletRequest request, @Validated @RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        String authorization_header = request.getHeader("Authorization");
        if( authorization_header != null) {
            throw new CustomException(ControllerMessage.PLEASE_LOGOUT);
        }
        Member member = memberService.saveMember(signupRequest);
        SignupResponse signupResponse = SignupResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .build();
        return new ApiResponse(HttpStatus.OK, signupResponse);
    }
}
