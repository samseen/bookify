package com.samseen.bookify.security;

import com.samseen.bookify.security.jwt.JwtAuthEntryPoint;
import com.samseen.bookify.security.jwt.JwtTokenFilter;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtTokenFilter jwtAuthTokenFilter;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(UserDetailsService userDetailsService, JwtAuthEntryPoint jwtAuthEntryPoint,
                             JwtTokenFilter jwtAuthTokenFilter, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    static BeanFactoryPostProcessor removeErrorSecurityFilter() {
        return (beanFactory) ->
                ((DefaultListableBeanFactory) beanFactory).removeBeanDefinition("errorPageSecurityInterceptor");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String[] WHITELIST = new String[]{
                "/",
                "/api/v1.0/auth/**",
                "/actuator/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/v1.0/user/create/admin"
        };

        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers(WHITELIST).permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
