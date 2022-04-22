package com.fitzhi.data;

import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class transports the {@link OpenIdToken} and {@link Staff} objects after the user registration.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@AllArgsConstructor(staticName = "of")
public @Data class OpenIdTokenStaff {
    
    private OpenIdToken openIdToken;

    private Staff staff;

}
