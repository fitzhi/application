package com.fitzhi.data.internal.github;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * A token emitted by the Gitbub authenticating url {@link https://github.com/login/oauth/access_token}.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@NoArgsConstructor (staticName = "of")
@AllArgsConstructor (staticName = "of")
public @Data class GithubToken {

	String access_token;

	String token_type;
	
	String scope;
	
}
