package com.fitzhi.source.crawler.git;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Author of GIT.
 */
@ToString()
@EqualsAndHashCode()
public @Data class Author {
    
    private final String name;

    private final String email;

    public Author(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
