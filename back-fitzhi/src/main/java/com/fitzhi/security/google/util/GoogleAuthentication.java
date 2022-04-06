package com.fitzhi.security.google.util;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Google authentication object representing the authenticating request.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class GoogleAuthentication implements Authentication  {
    
    boolean isAuthenticated;

    String userId;

    String name;

    public GoogleAuthentication (String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.isAuthenticated = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_TRUSTED_CLIENT"));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }

    @Override
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

}
