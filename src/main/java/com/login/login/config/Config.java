package com.login.login.config;

import com.login.login.service.idpwlogin.CustomAuthenticationProvider;
import com.login.login.service.idpwlogin.CustomIdPwLoginService;
import com.login.login.service.idpwlogin.CustomIdPwLoginSuccessHandler;
import com.login.login.service.oauth2.CustomOauth2UserService;
import com.login.login.service.oauth2.CustomOauthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class Config {
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomOauthLoginSuccessHandler customOauthLoginSuccessHandler;
    private final CustomIdPwLoginService customIdPwLoginService;
    private final CustomIdPwLoginSuccessHandler customIdPwLoginSuccessHandler;
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(customAuthenticationProvider())
                .formLogin(login -> login
                        .permitAll()
                        .successHandler(customIdPwLoginSuccessHandler)
                )
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
                        .successHandler(customOauthLoginSuccessHandler)
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService))
                )
                .cors(cors -> cors
                        .configurationSource(new CorsConfig()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/login/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .logout(logoutConfigurer -> logoutConfigurer.logoutSuccessUrl("/"));

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider customAuthenticationProvider(){
        return new CustomAuthenticationProvider(customIdPwLoginService,passwordEncoder());
    }
}
