/**
 * 
 */
package com.fitzhi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fitzhi.bean.StaffHandler;

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
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("loadUserByUsername(%s)", username));
		}
		return staffHandler.findStaffWithLogin(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("'%s' not found in Staff", username)));
		
	}

}
