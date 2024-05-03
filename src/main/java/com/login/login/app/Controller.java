package com.login.login.app;

import com.login.login.common.exception.ControllerMessage;
import com.login.login.common.exception.CustomException;
import com.login.login.domain.model.idpw.SignupRequest;
import com.login.login.domain.model.idpw.SignupResponse;
import com.login.login.domain.model.jwt.JwtAttributes;
import com.login.login.domain.model.response.ApiResponse;
import com.login.login.domain.service.jwt.JwtTokenProvider;
import com.login.login.infrastructure.entity.member.Member;
import com.login.login.infrastructure.persistent.rdbms.RDBMSMemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final JwtTokenProvider jwtTokenProvider;
    private final RDBMSMemberService memberService;
    @GetMapping("/login/refresh")
    public ApiResponse showJwt(HttpServletRequest request, HttpServletResponse response) {
        String refresh = jwtTokenProvider.resolveAccessToken(request);
        JwtAttributes jwtAttributes = jwtTokenProvider.jwtRefresh(refresh);
        return new ApiResponse(HttpStatus.OK,jwtAttributes);
    }
    /*
    회원가입 요청
     */
    @PostMapping("/login/signup")
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
    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request){
        String access = jwtTokenProvider.resolveAccessToken(request);
        jwtTokenProvider.expireToken(access);
        return new ApiResponse(HttpStatus.OK,"");
    }
}
