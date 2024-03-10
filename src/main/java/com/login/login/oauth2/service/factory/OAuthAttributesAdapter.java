package com.login.login.oauth2.service.factory;

import com.login.login.oauth2.service.dto.OAuthAttributes;

import java.util.Map;

public interface OAuthAttributesAdapter {
    OAuthAttributes toOAuthAttributes(Map<String,Object> attributes);
    boolean supports(String registrationId);
}
