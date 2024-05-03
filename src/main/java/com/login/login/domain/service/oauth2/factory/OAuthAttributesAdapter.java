package com.login.login.domain.service.oauth2.factory;

import com.login.login.domain.model.oauth2.OAuthAttributes;

import java.util.Map;

public interface OAuthAttributesAdapter {
    OAuthAttributes toOAuthAttributes(Map<String,Object> attributes);
    boolean supports(String registrationId);
}
