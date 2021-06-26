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

import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
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
	 * Detection template settings
	 */
	private final ExperienceDetectionTemplate detectionTemplate;

	/**
	 * Pattern to identify the eligible patterns.
	 */
	private final Pattern patternImport;

	/**
	 * Creation of a MarkAnnotationExpParser for the given codePattern
	 * @param project the current given project
	 * @param detectionTemplate the Detection template settings
	 */
	public static MarkAnnotationExpParser of(@NotNull Project project, @NotNull ExperienceDetectionTemplate detectionTemplate) {
		return new MarkAnnotationExpParser (project, detectionTemplate);
	}

	/**
	 * Construction.
	 * @param project the current given project
	 * @param detectionTemplate the Detection template settings
	 */
	private MarkAnnotationExpParser (@NotNull Project project, @NotNull ExperienceDetectionTemplate detectionTemplate) {
		this.project = project;
		this.detectionTemplate = detectionTemplate;
		this.pathGitDir = Paths.get(project.getLocationRepository());

		final Pattern pattern = Pattern.compile(detectionTemplate.getCodePattern());
		this.predicate = new Predicate<MarkerAnnotationExpr>() {
			@Override
			public boolean test(MarkerAnnotationExpr mae) {
				return pattern.matcher(mae.getNameAsString()).find();
			}
		};

		this.patternImport = Pattern.compile(detectionTemplate.getImportPattern());
	}

	/**
	 * Producing the equivalent of the command {@code git blame -L lineNumber,+1 filename}.
	 * 
	 * @param git the GIT repository
	 * @param filePath the filePath
	 * @param lineNumber the line number
	 */
	public PersonIdent getPersonIdent(Git git, String filePath, int lineNumber) throws ApplicationException {
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
	public void analyze(CompilationUnit compilationUnit, Git git, ProjectDetectedExperiences mapDetectedExperiences) throws ApplicationException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Analyzing file %s", compilationUnit.getStorage().get().getFileName()));
		}

		// Recherche des librairies importées
		compilationUnit.getImports();
		// Pour exemple recherche des annotations du bloc de code, filtrées par un prédicat
		compilationUnit.findAll(MarkerAnnotationExpr.class, this.predicate);


		if (!contains(compilationUnit.getImports(), this.patternImport)) {
			// Nothing to process.
			return;
		}
		List<MarkerAnnotationExpr> list = compilationUnit.findAll(MarkerAnnotationExpr.class, this.predicate);
		for (MarkerAnnotationExpr mae : list) {
			if (mae.getBegin().isPresent()) {
				Path pathRelative = pathGitDir.relativize(compilationUnit.getStorage().get().getPath());
				if (log.isDebugEnabled()) {
					log.debug (String.format("%s %s %s", compilationUnit.getStorage().get().getFileName(),
						mae,
						getPersonIdent(git, pathRelative.toString(), mae.getBegin().get().line)));
					// PersonIdent pi = getPersonIdent(git, pathRelative.toString(), mae.getBegin().get().line);
					// System.out.println(mae + ";" + pi.getName() + ";" + pi.getEmailAddress());
				}
				PersonIdent pi = getPersonIdent(git, pathRelative.toString(), mae.getBegin().get().line);
				Author author = new Author(pi.getName(), pi.getEmailAddress());
				DetectedExperience de = DetectedExperience.of(detectionTemplate.getIdEDT(), project.getId(), author);
				mapDetectedExperiences.inc(de);
			}
		}
	}

	private static boolean contains(@NotNull NodeList<ImportDeclaration> list, @NotNull Pattern pattern) {

		Predicate<ImportDeclaration> predicateImportDeclaration = (importDeclaration) -> {
			return pattern.matcher(importDeclaration.getNameAsString()).find();
		};

		return list.stream()
			.filter(predicateImportDeclaration)
			.findAny()
			.isPresent();
	}

}