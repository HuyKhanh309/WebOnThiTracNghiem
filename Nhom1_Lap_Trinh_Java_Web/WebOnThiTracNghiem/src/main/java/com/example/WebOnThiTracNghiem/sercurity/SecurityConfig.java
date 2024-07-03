package com.example.WebOnThiTracNghiem.sercurity;

import com.example.WebOnThiTracNghiem.service.AccountService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AccountService userService;
    @Bean
    public UserDetailsService userDetailsService() {
        AccountService sv=new AccountService();
        return  sv;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http)
            throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/styles/**","/css/**", "/js/**", "/", "/oauth/**",
                                "/register", "/error")
                        .permitAll()
                        .requestMatchers("/admin/subjects/edit/**", "/admin/subjects/add",
                                "/admin/subjects/delete","/admin/exams/edit/**", "/admin/exams/add",
                                "/admin/exams/delete","/admin/questions/edit/**", "/admin/questions/add",
                                "/admin/questions/delete","/admin","/admin/subjects","/admin/exams",
                                "/admin/questions", "/admin/exams/questions/add")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers("/user")
                        .hasAnyAuthority("ADMIN", "USER")
                        .anyRequest().authenticated()
                ).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                ).formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("hutech")
                        .rememberMeCookieName("hutech")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userDetailsService())
                ).exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403")
                ).sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)
                        .expiredUrl("/login")
                ).httpBasic(httpBasic -> httpBasic.realmName("hutech")
                ).build();
    }
}
