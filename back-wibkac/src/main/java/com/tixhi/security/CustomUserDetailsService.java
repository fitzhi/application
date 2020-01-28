/**
 * 
 */
package com.tixhi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tixhi.bean.StaffHandler;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	/**
	 * The logger.
	 */
	private Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class.getCanonicalName());

	@Autowired
	StaffHandler staffHandler;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("loadUserByUsername(%s)", username));
		}
		return staffHandler.findStaffWithLogin(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("'%s' not found in Staff", username)));
		
	}


}
