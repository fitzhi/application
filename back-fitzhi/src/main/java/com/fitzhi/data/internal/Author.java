package com.fitzhi.data.internal;

import org.apache.commons.validator.routines.EmailValidator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Author of a GIT commit.
 */
@ToString()
@EqualsAndHashCode()
public @Data class Author {
    
    private final String name;

    private final String email;

    /**
     * Construction of the Author of a commit.
     * @param name the required name
     * @param email the optional email
     */
    public Author(String name, String email) {
        this.name = name;
        if (email != null) {
            String sanityzedEmail = email.trim().toLowerCase();
            if (!sanityzedEmail.isEmpty()) {
               if (EmailValidator.getInstance().isValid(sanityzedEmail)) {
                   this.email = sanityzedEmail;
               } else {
                    this.email = null;
               }
           } else {
                this.email = null;
           }
        } else {
            this.email = null;
        }
    }

    public Author(String name) {
        this(name, null);
    }
}
