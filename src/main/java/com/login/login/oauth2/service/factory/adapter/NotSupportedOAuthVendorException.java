package com.login.login.oauth2.service.factory.adapter;

public class NotSupportedOAuthVendorException extends RuntimeException{
    public NotSupportedOAuthVendorException(String message){
        super(message);
    }
}
