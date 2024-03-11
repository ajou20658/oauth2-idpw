package com.login.login.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ControllerMessage customMessage;
    public CustomException(ControllerMessage message) {
        this.customMessage = message;
    }
}
