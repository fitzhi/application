package com.fitzhi.data.internal.github;

import lombok.Data;

/**
 * <p>
 * A token emitted by the Gitbub authenticating url {@link https://github.com/login/oauth/access_token}.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class GithubToken {

	String access_token;

	String token_type;
	
	String scope;
	
}
