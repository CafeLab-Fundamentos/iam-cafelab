package com.acme.iamcafelab.iam.infrastructure.authorization.sfs.pipeline;

import com.acme.iamcafelab.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.acme.iamcafelab.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);

    private final BearerTokenService tokenService;

    @Qualifier("defaultUserDetailsService")
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(
            BearerTokenService tokenService,
            UserDetailsService userDetailsService
    ) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String path = request.getRequestURI();

            if (isPublicPath(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = tokenService.getBearerTokenFrom(request);

            if (token == null) {
                LOGGER.debug("No Bearer token on {} {}", request.getMethod(), path);
            } else if (tokenService.validateToken(token)) {
                String email = tokenService.getUsernameFromToken(token);

                try {
                    var userDetails = userDetailsService.loadUserByUsername(email);

                    SecurityContextHolder.getContext()
                            .setAuthentication(
                                    UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request)
                            );

                } catch (UsernameNotFoundException ex) {
                    LOGGER.warn("JWT válido pero usuario IAM no encontrado para subject={}", email);
                }

            } else {
                LOGGER.debug("Bearer presente pero token rechazado en {}", path);
            }

        } catch (Exception e) {
            LOGGER.error("Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/authentication/")
                || path.equals("/api/v1/profiles")
                || path.startsWith("/api/v1/profiles/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars/")
                || path.equals("/")
                || path.equals("/error");
    }
}