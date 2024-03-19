package com.login.login.service.oauth2.factory.adapter;

import com.login.login.service.oauth2.dto.OAuthAttributes;
import com.login.login.service.oauth2.factory.OAuthAttributesAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KakaoOAuthAttributesAdapter implements OAuthAttributesAdapter {
    private static final String REGISTRATION_ID = "kakao";
    @Override
    public OAuthAttributes toOAuthAttributes(Map<String, Object> attributes) {
        log.info(attributes.toString());
        Map<String,Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String,Object> kakao_account=(Map<String, Object>) attributes.get("kakao_account");
        properties.put("id",attributes.get("id"));
        Map<String,Object> customAttribute = new HashMap<>();
        customAttribute.put("name",properties.get("nickname"));
        customAttribute.put("email", kakao_account.get("email"));
        customAttribute.put("profile", properties.get("profile_image"));
        return OAuthAttributes.builder()
                .name((String) properties.get("nickname"))
                .email((String) kakao_account.get("email"))
                .profile((String) properties.get("profile_image"))
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
