package com.login.login.oauth2.service;

import com.login.login.domain.member.Member;
import com.login.login.domain.member.MemberRepository;
import com.login.login.oauth2.service.dto.OAuthAttributes;
import com.login.login.oauth2.service.factory.OAuthAttributesAdapterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final OAuthAttributesAdapterFactory oAuthAttributesAdapterFactory;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        var attributes = oAuthAttributes(registrationId, oAuth2User);
        var member = saveOrUpdate(attributes);
        Map<String, Object> map = attributes.attributes();
        map.put("id", member.getId());
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(member.roleKey())),
                map,
                attributes.nameAttributeKey()
        );
    }
    private OAuthAttributes oAuthAttributes(String registrationId, OAuth2User oAuth2User){
        return oAuthAttributesAdapterFactory.factory(registrationId)
                .toOAuthAttributes(oAuth2User.getAttributes());
    }
    private Member saveOrUpdate(OAuthAttributes oAuthAttributes){
        Member member = memberRepository.findByEmail(oAuthAttributes.email())
                .map(entity -> entity.update(oAuthAttributes.name(), oAuthAttributes.profile()))
                .orElse(oAuthAttributes.toEntity());
        return memberRepository.save(member);
    }
}
