package com.fitzhi.source.crawler.javaparser;

import com.fitzhi.exception.ApplicationException;
import com.github.javaparser.ast.CompilationUnit;

import org.eclipse.jgit.api.Git;

/**
 * <p>
 * Code experience parser in a compilation unit.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface ExperienceParser {

	/**
	 * Analyze the given compilation unit and detect all identifiable experiences {@code (skill; level)}.
	 * 
	 * @param compilationUnit the given compilation unit.
	 * @param git the current Git repository
	 * @throws ApplicationException thrown if any problem occurs
	 */
	void analyze(CompilationUnit compilationUnit, Git git) throws ApplicationException;
}
