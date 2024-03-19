package com.login.login.service.oauth2.dto;

import lombok.NoArgsConstructor;
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
