package com.fitzhi.security.token.google.util;

import java.util.Arrays;
import java.util.Collection;

import com.fitzhi.data.internal.Staff;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static com.fitzhi.Global.GOOGLE_OPENID_SERVER;
import static com.fitzhi.Global.ROLE_TRUSTED_USER;

/**
 * Staff authentication object representing the authentication request based on the Staff information.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class StaffAuthentication implements Authentication  {
    
    boolean isAuthenticated;

    private final Staff staff;

    public StaffAuthentication (Staff staff) {
        this.staff = staff;
        this.isAuthenticated = true;
    }

    @Override
    public String getName() {
        return ((staff.getFirstName() != null) ? staff.getFirstName() : "") 
             + ((staff.getLastName() != null) ? staff.getLastName() : "");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(ROLE_TRUSTED_USER));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return staff;
    }

    @Override
    public Object getPrincipal() {
        return this.staff.getPrincipal(GOOGLE_OPENID_SERVER);
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
