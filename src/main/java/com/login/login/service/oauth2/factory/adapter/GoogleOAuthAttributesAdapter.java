package com.login.login.service.oauth2.factory.adapter;

import com.login.login.service.oauth2.dto.OAuthAttributes;
import com.login.login.service.oauth2.factory.OAuthAttributesAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class GoogleOAuthAttributesAdapter implements OAuthAttributesAdapter {
    public static final String REGISTRATION_ID="google";
    @Override
    public OAuthAttributes toOAuthAttributes(Map<String, Object> attributes) {
        Map<String,Object> customAttribute = new HashMap<>();
        customAttribute.put("name",attributes.get("name"));
        customAttribute.put("email", attributes.get("email"));
        customAttribute.put("profile", attributes.get("picture"));
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
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
