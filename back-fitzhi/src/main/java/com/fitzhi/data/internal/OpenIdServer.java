package com.fitzhi.data.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public @Data class OpenIdServer {
    private String serverId;
    private String clientId;
    private String clientSecret;
}

