package com.fitzhi.source.crawler.impl;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.CODE_IO_EXCEPTION;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.git.GitUtil;
import com.fitzhi.source.crawler.javaparser.ExperienceParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Implementation of the bean in charge of handling the ecosystem
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
@Slf4j
public class EcosystemAnalyzerImpl implements EcosystemAnalyzer {

	/**
	 * Directory where the referential data are stored.
	 */
	@Value("${referential.dir}")
	private String referentialDir;

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();
	
	/**
	 * Name of the file containing the settings required to detect a skill and its level in 
	 */
	private final String nameOfFileCodeLevelDetectionSettings = "experience-detection-template.json";

	/**
	 * Ecosystems.
	 */
	private Map<Integer, Ecosystem> ecosystems = null;
	
	/**
	 * @return the ecosystems declared in the application.
	 * @throws ApplicationException thrown if any problem occurs
	 */
	public Map<Integer, Ecosystem> getEcosystems() throws ApplicationException {
		if (ecosystems == null) {
			ecosystems = loadEcosystems();
		}
		return ecosystems;
	}
	
	@Override
	public Map<Integer, Ecosystem> loadEcosystems() throws ApplicationException {

		final File fileEcosystem = new File (referentialDir + "ecosystem.json"); 
		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading the file %s", fileEcosystem.getAbsolutePath()));
		}
		
		try (FileReader fr = new FileReader(fileEcosystem)) {
			Type listEcosystemsType = new TypeToken<List<Ecosystem>>(){}.getType();
			List<Ecosystem> listEcosystems = gson.fromJson(fr, listEcosystemsType);
			Map<Integer, Ecosystem> mapEcosystems = new HashMap<Integer, Ecosystem>();
			listEcosystems.forEach(ecosystem -> mapEcosystems.put(ecosystem.getId(), ecosystem));
			return mapEcosystems;
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, fileEcosystem.getAbsolutePath()), e);
		}
	}
	
	@Override
	public List<Ecosystem> detectEcosystems(Set<String> pathnames) throws ApplicationException {
		
		Map<Integer, Ecosystem> ecosystems = loadEcosystems();

		final Map<Integer, Integer> statOnEcosystems = new HashMap<>();
		for (int i = -1; i <= ecosystems.size(); i++) {
			statOnEcosystems.put(i, 0);
		}
		
		// List of patterns for the ecosystem detection 
		List<Pattern> patterns = ecosystems.values().stream()
				.map(Ecosystem::getPattern)
				.map(Pattern::compile)
				.collect(Collectors.toList());
		
		for (String pathname : pathnames) {
			int id = getIdEcosystem(pathname, patterns);
			statOnEcosystems.put(id, Integer.valueOf(statOnEcosystems.get(id).intValue()+1));
		}
		
		List<Entry<Integer, Integer>> sortedList = new ArrayList<Entry<Integer, Integer>>(statOnEcosystems.entrySet());
	    Collections.sort(sortedList, new Comparator<Entry<Integer, Integer>>() {

	        @Override
	        public int compare(Entry<Integer, Integer> obj1, Entry<Integer, Integer> obj2) {
	            return obj2.getValue().compareTo(obj1.getValue());
	        }
	    });

	    int k = 0;
	    //
	    // If most of the files are not recognized, there is certainly a configuration problem.
	    // Most probably an ecosystem is missing in the ecosystem.json.
	    //
	    if (sortedList.get(0).getKey() == -1) {
	    	log.error("An ecosystem is missing. Check the configuration in the file 'ecosystem.json'");
	    	k++;
	    }

	    //
	    // Detected ecosystems are represented by files.
	    // We start form 0 or 1, depending on the presence of undetected ecosystems.
	    //
	    List<Ecosystem> detectedEcosystems = new ArrayList<Ecosystem>();
	    for (int i = k; i < sortedList.size(); i++) {
	    	//
	    	// getValue() means activity detected for this ecosystem.
	    	// We only collect the known ecosystem e.g. (getKey() > 0)
	    	//
	    	if ((sortedList.get(i).getValue() > 0) & (sortedList.get(i).getKey() >= 0)){
	    		detectedEcosystems.add(ecosystems.get(sortedList.get(i).getKey()));
	    	}
	    }
		return detectedEcosystems;
	}
	
	/**
	 * @param pathname the source pathname to be evaluated
	 * @param  patternsEcosystem the patterns of ecosystem to be used
	 * @return the ecosystem identifier detected in the given pathname or -1 if none's found
	 */
	private int getIdEcosystem(String pathname, List<Pattern> patternsEcosystem) {
		for (int i=0; i<patternsEcosystem.size(); i++) {
			Matcher matcher = patternsEcosystem.get(i).matcher(pathname);
			if (matcher.find()) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Map<Integer, ExperienceDetectionTemplate> loadExperienceDetectionTemplates() throws ApplicationException {

		final File fileEcosystem = new File (referentialDir + nameOfFileCodeLevelDetectionSettings); 
		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading the file %s", fileEcosystem.getAbsolutePath()));
		}
		
		try (FileReader fr = new FileReader(fileEcosystem)) {
			Type listEcosystemsType = new TypeToken<List<ExperienceDetectionTemplate>>(){}.getType();
			List<ExperienceDetectionTemplate> listExperiencesDetectionTemplate = gson.fromJson(fr, listEcosystemsType);
			Map<Integer, ExperienceDetectionTemplate> mapExperienceDetectionTemplatess = new HashMap<Integer, ExperienceDetectionTemplate>();
			listExperiencesDetectionTemplate.forEach(edt -> mapExperienceDetectionTemplatess.put(edt.getIdEDT(), edt));
			return mapExperienceDetectionTemplatess;
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, fileEcosystem.getAbsolutePath()), e);
		}
	}

	@Override
	public void updateStaffExperience(Project project, ExperienceParser ...parsers) throws ApplicationException {

		try (Git git  = GitUtil.git(project)) {

			// We parse the repository.
			final ProjectRoot projectRoot = new ParserCollectionStrategy().collect(Paths.get(project.getLocationRepository()));
			for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
				List<ParseResult<CompilationUnit>> res = sourceRoot.tryToParse();
				for (ParseResult<CompilationUnit> pr : res) {
					if (pr.isSuccessful()) {
						if (pr.getResult().isPresent()) {
							CompilationUnit cu =  pr.getResult().get();
							for (ExperienceParser parser : parsers) {
								parser.analyze(cu, git);
							}
						}
					}
				}
			}
		} catch (final IOException ioe) {
			throw new ApplicationException (CODE_IO_EXCEPTION, ioe.getMessage());
		}
	}

}
