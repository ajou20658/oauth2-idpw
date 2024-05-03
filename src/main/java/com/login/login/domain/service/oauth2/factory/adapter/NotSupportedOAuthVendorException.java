package com.login.login.domain.service.oauth2.factory.adapter;

public class NotSupportedOAuthVendorException extends RuntimeException{
    public NotSupportedOAuthVendorException(String message){
        super(message);
    }
}
