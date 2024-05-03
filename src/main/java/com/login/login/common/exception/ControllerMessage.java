package com.login.login.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Getter

public enum ControllerMessage {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 오류 발생"),
    INVALID_MEMBER(HttpStatus.BAD_REQUEST,"존재하지 않는 유저입니다."),
    INVALID_MEMBER_REMOVED(HttpStatus.BAD_REQUEST,"탈퇴한 회원"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 요청"),
    RE_LOGIN(HttpStatus.BAD_REQUEST,"재 로그인 필요"),
    PLEASE_LOGIN(HttpStatus.BAD_REQUEST, "로그인 필요"),
    DUP_EMAIL(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일"),
    BAD_ROLE(HttpStatus.BAD_REQUEST, "올바르지 않는 Role(ROLE_POLICE/ROLE_FIRE)"),
    PLEASE_LOGOUT(HttpStatus.BAD_REQUEST,"로그아웃 필요"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST,"만료된 토큰. 재갱신 요청 필요"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"리프레시 토큰 만료. 재 로그인 필요"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"유효하지 않은 토큰");
    ControllerMessage(HttpStatus httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
    private final HttpStatus httpStatus;
    private final String message;
}
