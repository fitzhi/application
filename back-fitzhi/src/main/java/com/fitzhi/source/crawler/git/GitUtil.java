package com.fitzhi.source.crawler.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fitzhi.data.internal.Project;
import com.fitzhi.source.crawler.impl.EcosystemAnalyzerImpl;

import org.eclipse.jgit.api.Git;

/**
 * <p>
 * This class is containing static methods used by {@link GitCrawler} or {@link EcosystemAnalyzerImpl}.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public final class GitUtil {
    
    /**
	 * @return the location repository entry point <br/>
	 *         <i>i.e. the absolute path to the .git file.</i>
	 */
	public static String getLocalDotGitFile(Project project) {
		return (project.getLocationRepository()
				.charAt(project.getLocationRepository().length() - 1) == File.pathSeparatorChar)
						? project.getLocationRepository() + ".git"
						: project.getLocationRepository() + "/.git";
	}

    /**
     * Instanciate the {@code git} framework associated with the local repository.
     * @param project the given project
     * @return the {@code git} framework associated with the local repository.
     * @throws IOException
     */
    public static Git git(Project project) throws IOException {
        return Git.open(Paths.get(getLocalDotGitFile(project)).toFile());
    }
}
