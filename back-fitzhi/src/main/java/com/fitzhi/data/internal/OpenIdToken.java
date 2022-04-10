package com.fitzhi.data.internal;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a decoded OpenID token.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@NoArgsConstructor (staticName = "of")
@AllArgsConstructor
public @Data class OpenIdToken {

    public boolean inError = false;

    public String serverId;
    
    private String userId;

    private String email;
    private boolean emailVerified;

    private String givenName;
    private String familyName;
    private String name;

    private String locale;

    private Date expirationDate;
    private Date issuedDate;
    
    /**
     * Origin of the content. First implemented origin is GooleToken;
     */
    private Object origin;

    /**
     * Create an OpenId Token in error if the decoding process failed.
     * @return the openId token in error
     */
    public static OpenIdToken error() {
        OpenIdToken oit = of();
        oit.setInError(true);
        return oit;
    }
}
