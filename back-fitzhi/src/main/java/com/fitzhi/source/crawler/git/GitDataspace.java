package com.fitzhi.source.crawler.git;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import lombok.Data;

/**
 * <p>
 * This class is the dataspace for the Git operations. 
 * This dataspace is mainly used  for performance purpose.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class GitDataspace {

    private int idProject;

    Map<ObjectId, RevCommit> allCommits = new HashMap<>();

    /**
     * Build an instance of cache.
     * @param idProject the project concerned by this cache.
     */
    public GitDataspace(int idProject) {
        this.idProject = idProject;
    }

    /**
     * Test the presence of a GIT reference in the cache.
     * @param id the GIT object reference identifier
     * @return {@code true} if the reference has already been stored in the cache, {@code false} otherwise. 
     */
    public boolean containsKey(ObjectId id) {
        return allCommits.containsKey(id);
    }

    /**
     * Refister a Commit entry in the cache
     * @param commit the Commit to be referenced.
     */
    public void addCommit(RevCommit commit) {
        allCommits.put(commit.getId(), commit);
    }

    /**
     * @return the collection of commits registered in the cache.
     */
    public Collection<RevCommit> registeredCommits() {
        return allCommits.values();
    }
}