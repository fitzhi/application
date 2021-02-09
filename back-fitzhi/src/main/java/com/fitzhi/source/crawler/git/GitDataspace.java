package com.fitzhi.source.crawler.git;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;

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

    /**
     * This map associates hash reference to its commit tree ObjectId  
     */
    final Map<String, ObjectId> cacheTrees = new HashMap<String, ObjectId>();
			

    /**
     * Build an instance of cache.
     * @param idProject the project concerned by this cache.
     */
    public GitDataspace(int idProject) {
        this.idProject = idProject;
    }

    /**
     * Test the presence of a GIT tree reference in the cache.
     * @param id the GIT tree reference identifier
     * @return {@code true} if the reference has already been stored in the cache, {@code false} otherwise. 
     */
    public boolean containsKey(String id) {
        return cacheTrees.containsKey(id);
    }

    /**
     * Register a commit tree entry into the cache
     * @param id the tree hashed identifier 
     * @param tree the tree reference.
     */
    public void addTree(String id, ObjectId tree) {
        cacheTrees.put(id, tree);
    }

    /**
     * Return the commit tree reference, stored into the cache.
     * @param id the tree identifier
     * @return the associated tree reference, or {@code null} if none exists.
     * @see {@link Map#get(Object)}
     */
    public ObjectId getTree(String id) {
        return cacheTrees.get(id);
    }

}