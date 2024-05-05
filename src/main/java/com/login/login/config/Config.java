package com.login.login.config;

import com.login.login.domain.service.idpw.CustomAuthenticationProvider;
import com.login.login.domain.service.idpw.CustomIdPwLoginService;
import com.login.login.domain.service.oauth2.CustomLoginSuccessHandler;
import com.login.login.domain.service.oauth2.CustomOauth2UserService;
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
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomIdPwLoginService customIdPwLoginService;
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(customAuthenticationProvider())
                .formLogin(login -> login
                        .permitAll()
                        .successHandler(customLoginSuccessHandler)
                )
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
                        .successHandler(customLoginSuccessHandler)
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
