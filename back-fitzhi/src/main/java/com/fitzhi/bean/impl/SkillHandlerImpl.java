package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.CODE_SKILL_NOFOUND;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_SKILL_NOFOUND;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Main (an currently single) implementation in charge of the skills management
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Component
public class SkillHandlerImpl extends AbstractDataSaverLifeCycleImpl implements SkillHandler {

	/**
	 * The skills collection.
	 */
	private Map<Integer, Skill> skills;

	/**
	 * Data access interface.
	 */
	@Autowired
	DataHandler dataSaver;

	@Override
	public Map<Integer, Skill> getSkills() {
		
		if (this.skills != null) {
			return this.skills;
		}

		try {
			this.skills = dataSaver.loadSkills();
			if (log.isDebugEnabled()) {
				log.debug(String.format("%d %s", this.skills.size(), "skills loaded"));
			}
		} catch (final ApplicationException e) {
			// Without skills, this application is absolutely not viable
			throw new ApplicationRuntimeException(e);
		}
		return this.skills;		
	}

	@Override
	public Optional<Skill> lookup(final String skillTitle) {
		return getSkills().values().stream()
				.filter((Skill skill) -> skill.getTitle().equalsIgnoreCase(skillTitle.toUpperCase())).findFirst();
	}

	@Override
	public Skill addNewSkill(final Skill skill) {
		synchronized (lockDataUpdated) {
			int id = getSkills().size()+1;
			skill.setId(id);
			getSkills().put(id, skill);
			this.dataUpdated = true;
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("Skill added %s", skill.toString()));
		}
		return skill;
	}

	@Override
	public boolean containsSkill(final int idSkill) {
		return getSkills().containsKey(idSkill);
	}

	@Override
	public void saveSkill(final Skill skill) throws ApplicationException {
		if (skill.getId() == 0) {
			throw new ApplicationException(CODE_SKILL_NOFOUND, MessageFormat.format(MESSAGE_SKILL_NOFOUND, skill.getId()));
		}
		synchronized (lockDataUpdated) {
			getSkills().put(skill.getId(), skill);
			this.dataUpdated = true;
		}
	}

	@Override
	public Skill getSkill(int idSkill) throws ApplicationException {
		
		Skill skill = getSkills().get(idSkill);
		if (skill == null) {
			throw new ApplicationException(CODE_SKILL_NOFOUND, MessageFormat.format(MESSAGE_SKILL_NOFOUND, idSkill));
		}
		
		return skill;
	}

	@Override
	public Map<Integer, String> detectorTypes() throws ApplicationException {
		return SkillDetectorType.getDetectorTypes();
	}

	@Override
	public Map<Integer, ProjectSkill> extractSkills(String rootPath, List<CommitHistory> entries) throws ApplicationException {

		Set<Skill> candidateSkills = getSkills().values().stream()
			.filter(skill -> skill.getDetectionTemplate() != null)
			.collect(Collectors.toSet());
		if (log.isDebugEnabled()) {
			candidateSkills.stream().map(Skill::getTitle).forEach(log::debug);
		}
		
		Map<Integer, ProjectSkill> extractedSkills = new HashMap<>();
	
		for (Skill skill : candidateSkills) {
			for (CommitHistory entry : entries) {
				if (isSkillDetected(skill, rootPath, entry.getSourcePath())) {
					ProjectSkill projectSkill = extractedSkills.get(skill.getId());
					if (projectSkill == null) {
						if (log.isDebugEnabled()) {
							log.debug(String.format("The skill %s has been detected in the project", skill.getTitle()));
						}
						extractedSkills.put(skill.getId(), new ProjectSkill(skill.getId(), 1, fileSize(rootPath, entry.getSourcePath())));
					} else {
						projectSkill.incNumberOfFiles();
						projectSkill.addFileSize(fileSize(rootPath, entry.getSourcePath()));
					}
				}
			}			
		}
		
		return extractedSkills;
	}

	private long fileSize (String rootPath, String sourcePath) {
		try {
			File file = new File(rootPath + File.separatorChar + sourcePath);
			return file.length();
		} catch (final Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			// A file not found is considered as an empty file size. 
			return 0;
		}
	}
	
	/**
	 * Test if the passed skill is detected through this file.
	 * @param skill the skill candidate to be detected
	 * @param rootPath the rootPath where the repository has been cloned
	 * @param sourcePath the path of a source file 
	 * @return {@code true} if this skill is detected, {@code false} otherwise
	 * @throws ApplicationException exception thrown if any problem occurs (most probably an IOException)
	 */
	private boolean isSkillDetected(Skill skill, String rootPath, String sourcePath) throws ApplicationException {
		switch (skill.getDetectionTemplate().getDetectionType()) {
		case FILENAME_DETECTOR_TYPE:
			return checkFilenamePattern(sourcePath, skill.getDetectionTemplate().getPattern());
		case PACKAGE_JSON_DETECTOR_TYPE:
			return checkFilePattern("package.json", rootPath, sourcePath, skill.getDetectionTemplate().getPattern());
		case POM_XML_DETECTOR_TYPE:
			return checkFilePattern("pom.xml", rootPath, sourcePath, skill.getDetectionTemplate().getPattern());
		}
		throw new ApplicationRuntimeException("Should not pass here!");
	}
	
	@Override
	public boolean isSkillDetectedWithFilename(Skill skill, String sourcePath) {
		
		if (skill.getDetectionTemplate() == null) {
			return false;
		}
		switch (skill.getDetectionTemplate().getDetectionType()) {
			case FILENAME_DETECTOR_TYPE:
				return checkFilenamePattern(sourcePath, skill.getDetectionTemplate().getPattern());
			default: 
				return false;
		}
	}

	/**
	 * Check if the given source pathname verifies the given pattern
	 * @param sourcePath the source pathname
	 * @param pattern the pattern to be verified
	 * @return {@code true} if this skill is detected, {@code false} otherwise
	 */
	private boolean checkFilenamePattern(String sourcePath, String pattern) {
		Matcher matcher = Pattern.compile(pattern).matcher(sourcePath);
		return (matcher.find());		
	}	

	@Override
	public boolean checkFilePattern(String filenameDependencies, String rootPath, String sourcePath, String dependency) throws ApplicationException {
	
		try {
			if (sourcePath.indexOf(filenameDependencies) == -1) {
				return false;
			}
			File file = new File(rootPath + File.separatorChar + sourcePath);
			if (!file.exists()) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("File %s does not exist", file.getAbsolutePath()));
				}
				return false;
			}
			try (FileReader reader = new FileReader(file)) {
				BufferedReader br = new BufferedReader(reader);
				return br.lines().anyMatch(line -> (line.indexOf(dependency) != -1));
			} catch (final Exception e) {
				throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, file.getAbsoluteFile()), e);
			}
		} catch (final ApplicationException e) {
			if (log.isDebugEnabled()) {
				log.debug("Application exception", e);
			}
			throw e;
		}
	}	
	
}
