package com.login.login;

import com.login.login.jwt.*;
import com.login.login.jwt.config.JwtAccessDeniedHandler;
import com.login.login.jwt.config.JwtAuthenticationEntryPoint;
import com.login.login.jwt.config.JwtFilter;
import com.login.login.oauth2.service.CustomOauth2UserService;
import com.login.login.oauth2.service.factory.CustomOAuth2LoginSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

@EnableWebSecurity
//@Configuration
@RequiredArgsConstructor
@Slf4j
public class Config {
    private final CustomOauth2UserService customOauth2UserService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final JwtTokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(redisTemplate,tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/login/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
//                        .successHandler(new AuthenticationSuccessHandler() {
//                            @Override
//                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                                log.info("authentication = {}",authentication);
//                                OAuth2Usear oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//                                if(authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken){
//                                    TokenDto token = tokenProvider.generateToken(authentication);
//                                    log.info("token : {}", token);
//                                    response.addHeader("Authorization", "Bearer " + token.getAccessToken());
//                                    response.addHeader("RefreshToken", "Bearer " + token.getRefreshToken());
//                                }
//                            }
//                        })
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService))
//                        .redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig
//                                .baseUri("/"))
                )

                .logout(logoutConfigurer -> logoutConfigurer.logoutSuccessUrl("/"));
//        logFilterChain(http);

        return http.build();
    }

//    private void logFilterChain(HttpSecurity httpSecurity) throws Exception{
//        FilterChainProxy filterChainProxy = getFilterChainProxy(httpSecurity);
//
//        // 필터 체인에서 필터들을 가져와서 로그로 출력
//        List<Filter> filters = filterChainProxy.getFilters("/");
//
//        for (Filter filter : filters) {
//            log.info("Activated Filter: " + filter.getClass().getSimpleName());
//        }
//    }
//    private FilterChainProxy getFilterChainProxy(HttpSecurity http) throws Exception {
//        // 필터 체인 프록시를 가져오기 위해 적절한 클래스 이름으로 캐스팅
//        return (FilterChainProxy) http.getSharedObject(FilterChainProxy.class);
//    }
}
