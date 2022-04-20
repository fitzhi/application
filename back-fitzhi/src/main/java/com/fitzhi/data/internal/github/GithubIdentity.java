package com.fitzhi.data.internal.github;

import lombok.Data;

/**
 * <p>
 * The GitHub user information retrieved from the url {@link https://api.github.com/user}.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class GithubIdentity {
    private String id;
    private String email;
    private String login;
    private String name;
    private String bio;
}
