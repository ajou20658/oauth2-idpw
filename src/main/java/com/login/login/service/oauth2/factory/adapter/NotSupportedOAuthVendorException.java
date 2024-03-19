package com.login.login.service.oauth2.factory.adapter;

public class NotSupportedOAuthVendorException extends RuntimeException{
    public NotSupportedOAuthVendorException(String message){
        super(message);
    }
}
