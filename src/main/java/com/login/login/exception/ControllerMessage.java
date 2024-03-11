package com.login.login.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Getter

public enum ControllerMessage {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 오류 발생"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 요청"),
    RE_LOGIN(HttpStatus.BAD_REQUEST,"재 로그인 필요"),
    PLEASE_LOGIN(HttpStatus.BAD_REQUEST, "로그인 필요"),
    EXPIRED_ACCESSTOKEN(HttpStatus.OK,"만료된 토큰. 재갱신 요청 필요"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"유효하지 않은 토큰");
    ControllerMessage(HttpStatus httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
    private final HttpStatus httpStatus;
    private final String message;
}
