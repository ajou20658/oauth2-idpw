package com.login.login.oauth2.service.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@NoArgsConstructor
public class ApiResponse {
    public HttpStatus httpStatus;
    public Long code;
    public Object data;
    public ApiResponse(HttpStatus httpStatus, Long code, Object data) {
        this.httpStatus = httpStatus;
        this.code=code;
        this.data=data;
    }
    public ApiResponse(HttpStatus httpStatus, Object data) {
        this.httpStatus = httpStatus;
        this.code= (long) HttpStatus.OK.value();
        this.data=data;
    }

}
