package com.samseen.bookify.security.jwt;

import com.samseen.bookify.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    public JwtTokenFilter(TokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        getToken(request).ifPresent(token -> {
            if (tokenService.isValid(token)) {
                String username = tokenService.getUserNameFromJwtToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        });
        filterChain.doFilter(request, response);
    }

    public Optional<String> getToken(HttpServletRequest request) {
        String tokenPrefix = "Bearer ";
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.isNotBlank(header) && header.contains(tokenPrefix) ?
                Optional.ofNullable(header.substring(tokenPrefix.length())) :
                Optional.empty();
    }
}
