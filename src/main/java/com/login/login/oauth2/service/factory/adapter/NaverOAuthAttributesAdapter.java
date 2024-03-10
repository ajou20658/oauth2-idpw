package com.login.login.oauth2.service.factory.adapter;

import com.login.login.oauth2.service.dto.OAuthAttributes;
import com.login.login.oauth2.service.factory.OAuthAttributesAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
@Slf4j
public class NaverOAuthAttributesAdapter implements OAuthAttributesAdapter {
    private static final String REGISTRATION_ID = "naver";
    @Override
    public OAuthAttributes toOAuthAttributes(Map<String, Object> attributes) {
        log.info(attributes.toString());
        Map<String,Object> response = (Map<String, Object>) attributes.get("response");
        Map<String,Object> customAttribute = new HashMap<>();
        customAttribute.put("profile", response.get("profile_image"));
        customAttribute.put("email", response.get("email"));
        customAttribute.put("name", response.get("name"));
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .profile((String) response.get("profile_image"))
                .attributes(customAttribute)
                .nameAttributeKey("name")
                .registrationId(REGISTRATION_ID)
                .build();
    }

    @Override
    public boolean supports(String registrationId) {
        return REGISTRATION_ID.equals(registrationId);
    }
}
