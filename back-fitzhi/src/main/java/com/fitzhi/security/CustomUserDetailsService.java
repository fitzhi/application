package com.fitzhi.security;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	StaffHandler staffHandler;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("loadUserByUsername(%s)", username));
		}
		Staff staff = staffHandler.findStaffOnLogin(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("'%s' not found in Staff", username)));

		return staff;
		
	}

}
