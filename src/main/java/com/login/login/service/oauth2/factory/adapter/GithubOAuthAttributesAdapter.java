package com.login.login.service.oauth2.factory.adapter;

import com.login.login.service.oauth2.dto.OAuthAttributes;
import com.login.login.service.oauth2.factory.OAuthAttributesAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GithubOAuthAttributesAdapter implements OAuthAttributesAdapter {
    private static final String REGISTRATION_ID = "github";
    @Override
    public OAuthAttributes toOAuthAttributes(Map<String, Object> attributes) {
        log.info(attributes.toString());
        Map<String,Object> customAttribute = new HashMap<>();
        customAttribute.put("email",attributes.get("email"));
        customAttribute.put("name", attributes.get("name"));
        customAttribute.put("profile", attributes.get("avatar_url"));
        return OAuthAttributes.builder()
                .name((String) attributes.get("login"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("avatar_url"))
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
