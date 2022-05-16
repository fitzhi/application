package com.fitzhi.data.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public @Data class OpenId {
	private String serverId;
	private String userId;
}
