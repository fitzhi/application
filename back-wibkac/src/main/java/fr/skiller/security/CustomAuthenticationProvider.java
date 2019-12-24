package fr.skiller.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
	@Autowired
	StaffHandler staffHandler;
	
    @Override
    public Authentication authenticate(Authentication authentication)  {
  
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
         
        Optional<Staff> oStaff = staffHandler.findStaffWithLogin(name);
        
        if (!oStaff.isPresent()) return null;
        
        try {
			if (oStaff.get().isValidPassword(password)) {
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			    return new UsernamePasswordAuthenticationToken(
			      name, password, authorities);
			}
		} catch (SkillerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}