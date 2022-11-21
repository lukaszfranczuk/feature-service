package com.capco.interview.features.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLES_DELIMITER = ",";

    private final String expirationTime;
    private final String secret;

    public JwtService(@Value("${jwt.config.expirationTime}") String expirationTime,
                      @Value("${jwt.config.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.secret = secret;
    }

    void addAuthentication(HttpServletResponse res, Authentication authentication) {
        String jwt = createJwt(authentication.getName(), getRolesAsString(authentication));
        res.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + jwt);
    }

    Authentication getAuthentication(HttpServletRequest request) {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(jwt)) {
            return null;
        }
        String jwtToken = retrieveJwt(jwt);
        Jws<Claims> jwtObject;
        try {
            jwtObject = parseJwt(jwtToken);
        } catch (AuthorizationServiceException e) {
            return null;
        }
        String user = jwtObject.getBody().getSubject();
        String roles = (String) jwtObject.getBody().get(ROLES_CLAIM);
        List<SimpleGrantedAuthority> authorities = getRoles(roles);
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    private Jws<Claims> parseJwt(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException e) {
            throw new AuthorizationServiceException("Invalid token");
        }
    }

    private String retrieveJwt(String jwt) {
        return jwt.replace(TOKEN_PREFIX, StringUtils.EMPTY);
    }

    private String getRolesAsString(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        return String.join(ROLES_DELIMITER, roles);
    }

    private String createJwt(String username, String roles) {
        long currentTime = System.currentTimeMillis();
        long jwtExpirationTime = currentTime + Long.parseLong(expirationTime);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(username)
                .claim(ROLES_CLAIM, roles)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(jwtExpirationTime))
                .signWith(key)
                .compact();
    }

    private List<SimpleGrantedAuthority> getRoles(String roles) {
        List<String> stringRoles = Arrays.asList(roles.split(ROLES_DELIMITER));
        return stringRoles.stream()
                .filter(StringUtils::isNotBlank)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
