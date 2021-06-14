package com.fitzhi.source.crawler.javaparser;

import static com.fitzhi.Error.CODE_GIT_ERROR;
import static com.fitzhi.Error.MESSAGE_GIT_ERROR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.PersonIdent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkAnnotationExpParser implements ExperienceParser {

	/**
	 * The current active project.
	 */
	private final Project project;

	/**
	 * The path where the project local repository has been cloned.
	 */
	private final Path pathGitDir;

	/**
	 * The Predicate initialized for teh given codePattern
	 */
	private final Predicate<MarkerAnnotationExpr> predicate;

	/**
	 * Creation of a MarkAnnotationExpParser for the given codePattern
	 * @param project the current given project
	 * @param codePattern the code pattern to identify a remarkable piece of code
	 */
	public static MarkAnnotationExpParser of(@NotNull Project project, @NotNull String codePattern) {
		return new MarkAnnotationExpParser (project, codePattern);
	}

	/**
	 * Construction.
	 * @param project the current given project
	 * @param codePattern the code pattern
	 */
	private MarkAnnotationExpParser (Project project, String codePattern) {
		this.project = project;
		this.pathGitDir = Paths.get(project.getLocationRepository());
		final Pattern pattern = Pattern.compile(codePattern);
		this.predicate = new Predicate<MarkerAnnotationExpr>() {
			@Override
			public boolean test(MarkerAnnotationExpr mae) {
				return pattern.matcher(mae.getNameAsString()).find();
			}
		};
	}

	/**
	 * Producing the equivalent of the command {@code git blame -L lineNumber,+1 filename}.
	 * 
	 * @param git the GIT repository
	 * @param filePath the filePath
	 * @param lineNumber the line number
	 */
	public PersonIdent getAuthor(Git git, String filePath, int lineNumber) throws ApplicationException {
		try {
			final BlameResult blameResult = git.blame().setFilePath(filePath).setFollowFileRenames(true).call();
			if (blameResult == null) {
				throw new ApplicationException(CODE_GIT_ERROR,
					String.format("Cannot blame file %s @ %d", filePath, lineNumber));
			}
			return blameResult.getSourceCommitter(lineNumber);
		} catch (final GitAPIException gitException) {
			throw new ApplicationException(
				CODE_GIT_ERROR, 
				MessageFormat.format(MESSAGE_GIT_ERROR, project.getId(), project.getName())); 
		}
	} 

	@Override
	public void analyze(CompilationUnit compilationUnit, Git git) throws ApplicationException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Analyzing file %s", compilationUnit.getStorage().get().getFileName()));
		}
		List<MarkerAnnotationExpr> l = compilationUnit.findAll(MarkerAnnotationExpr.class, this.predicate /* Predicate.isEqual(new MarkerAnnotationExpr("Service")) */ );
		for (MarkerAnnotationExpr mae : l) {
			if (mae.getBegin().isPresent()) {
				Path pathRelative = pathGitDir.relativize(compilationUnit.getStorage().get().getPath());
				if (log.isInfoEnabled()) {
					log.info (String.format("%s %s %s", compilationUnit.getStorage().get().getFileName(),
						mae,
						getAuthor(git, pathRelative.toString(), mae.getBegin().get().line)));
				}
			}
		}
	}
}