package com.fitzhi.util;

import com.fitzhi.ApplicationRuntimeException;

/**
 * Public static usefull functions. 
 */
public abstract class CommonUtil {
    
    /**
     * Extract a project name candidate corresponding with the given git url.
     * @param repo the repository url
     * @return a possible project name
     */
    public static String extractProjectNameFromUrl(String repo) {
        int len = repo.length();
        if (".git".equals(repo.substring(len - 4))) {
            repo = repo.substring(0, len - 4);
        }
        int last = repo.lastIndexOf("/");
        if (last == -1) {
            throw new ApplicationRuntimeException(String.format("Invalid url %s.", repo));
        }
        return repo.substring(last + 1);
    }
}
