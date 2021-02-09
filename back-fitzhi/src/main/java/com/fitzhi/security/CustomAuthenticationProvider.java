package com.fitzhi.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
	@Autowired
	StaffHandler staffHandler;
	
    @Override
    public Authentication authenticate(Authentication authentication)  {
  
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
         
        Optional<Staff> oStaff = staffHandler.findStaffOnLogin(name);
        
        if (!oStaff.isPresent()) {
        	throw new BadCredentialsException(String.format("Invalid login %s", name));
        }
        
        try {
			if (oStaff.get().isValidPassword(password)) {
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			    return new UsernamePasswordAuthenticationToken(
			      name, password, authorities);
			} else {
				throw new BadCredentialsException(String.format("Invalid login/password for %s", name));
			}
		} catch (ApplicationException e) {
			log.error("Internal error", e);
		}
        
        return null;
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}