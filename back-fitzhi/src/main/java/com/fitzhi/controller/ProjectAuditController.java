package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_CANNOT_RETRIEVE_ATTACHMENTFILE;
import static com.fitzhi.Error.LIB_CANNOT_RETRIEVE_ATTACHMENTFILE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamAuditEntries;
import com.fitzhi.controller.in.BodyParamProjectAttachmentFile;
import com.fitzhi.data.internal.AttachmentFile;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.TopicWeight;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * Controller in charge the collection of {@link AuditTopic} declared in the project.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
@Api(
	tags="Project Audit controller API",
	description = "API endpoints to manage the audits realized for the application."
)
public class ProjectAuditController extends BaseRestController {

	@Autowired
	ProjectAuditHandler projectAuditHandler;
	
	@Autowired
	ProjectHandler projectHandler;

	/**
	 * Storage service dedicated to 
	 * /download the audit attachments.
	 */
	@Autowired
	@Qualifier("Attachment")
	StorageService storageService;
	
	@ResponseBody
	@PutMapping(path="/{idProject}/audit/topic/{idTopic}")
	public boolean addTopic(
		final @PathVariable("idProject") int idProject,
		final @PathVariable("idTopic") int idTopic) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST verb on /api/project/%d/audit/topic/%d", idProject, idTopic));
		}
		
		projectAuditHandler.addTopic(idProject, idTopic);
		return true;
	}
	
	/**
	 * load and return an {@link AuditTopic}
	 * @param idProject the project identifier
	 * @param idTopic the topic identifier
	 * @return the auditTopic found.
	 */
	@ResponseBody
	@GetMapping(path="/{idProject}/audit/topic/{idTopic}")
	public AuditTopic getTopicAudit(
			@PathVariable("idProject") int idProject,
			@PathVariable("idTopic") int idTopic) throws ApplicationException {

		AuditTopic auditProject = projectAuditHandler.getTopic(idProject, idTopic);
		return auditProject;
	}

	@ResponseBody
	@DeleteMapping(path="/{idProject}/audit/topic/{idTopic}")
	public boolean removeTopic(
		@PathVariable("idProject") int idProject,
		@PathVariable("idTopic") int idTopic) throws ApplicationException {
	
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"DELETE verb on /api/project/%d/audit/topic/%d", idProject, idTopic));
		}
		
		projectAuditHandler.removeTopic(idProject, idTopic, false);
		projectAuditHandler.processAndSaveGlobalAuditEvaluation(idProject);			
		return true;
			
	}

	@ResponseBody
	@ApiOperation(value="Save the evaluation given to an audit topic (architecture, design, performance...).")
	@PutMapping(path="/{idProject}/audit/{idTopic}/evaluation/{evaluation}")
	public boolean saveEvaluation(
			@PathVariable("idProject") int idProject,
			@PathVariable("idTopic") int idTopic,
			@PathVariable("evaluation") int evaluation) throws ApplicationException {
	
		if (log.isDebugEnabled()) {
			log.debug(String.format("POST verb on /project/%d/audit/%d/evaluation/%d", idProject, idTopic, evaluation));
		}
		
		projectAuditHandler.saveEvaluation(idProject, idTopic, evaluation);
		projectAuditHandler.processAndSaveGlobalAuditEvaluation(idProject);
		return true;
	}
	
	@ResponseBody
	@ApiOperation(value="Save the report given to an evaluated topic. This is the free text explanation of a numerical rate")
	@PutMapping(path="/{idProject}/audit/{idTopic}/report")
	public boolean saveReport(
		@PathVariable("idProject") int idProject,
		@PathVariable("idTopic") int idTopic,
		@RequestBody String report) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("PUB verb command on /api/project/%d/audit/%d/reporti", idProject, idTopic));
		}
		
		projectAuditHandler.saveReport(idProject, idTopic, report);
		return true;
	}

	@PutMapping(path="/{idProject}/audit/weights")
	@ApiOperation(value="Save the weights attributed for all topics in the audit. For each topic, its relative evaluation is equal to (evaluation*weight).")
	public ResponseEntity<Boolean> saveWeight(
		@PathVariable("idProject") int idProject,
		@RequestBody TopicWeight[] weights) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("PUT verb on /api/project/%d/audit/weights", idProject));
		}

		projectAuditHandler.saveWeights(idProject, Arrays.asList(weights));
		projectAuditHandler.processAndSaveGlobalAuditEvaluation(idProject);
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
	}
	
	@ResponseBody
	@ApiOperation(value="Save the reference of a file attached to an audit topic.")
	@PutMapping(path="{idProject}/audit/{idTopic}/attachmentFile")
	public boolean saveAttachmentFile(
		@PathVariable("idProject") int idProject,
		@PathVariable("idTopic") int idTopic,		
		@RequestBody AttachmentFile attachmentFile) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("PUT verb on /api/project/%d/audit/%d/attachmentFile", idProject, idTopic));
		}
		projectAuditHandler.updateAttachmentFile (idProject, idTopic, attachmentFile);
		return true;
	}
	
	@ResponseBody
	@DeleteMapping(path="{idProject}/audit/{idTopic}/attachmentFile/{idFile}")
	@ApiOperation(value="Remove the reference of a file attached to an audit topic.")
	public boolean removeAttachmentFile(
		@PathVariable("idProject") int idProject,
		@PathVariable("idTopic") int idTopic,
		@PathVariable("idFile") int idFile) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("DELETE verb on /api/project/%d/audit/%d/attachmentFile/%d", idProject, idTopic, idFile));
		}
		
		projectAuditHandler.removeAttachmentFile (idProject, idTopic, idFile);			
		return true;
	}
	
	/**
	 * Upload an attachment to an audit topic
	 * @param file the attachment file
	 * @param idProject the project identifier
	 * @param idTopic the topic identifier
	 * @param type the type of file (WORD, PDF...)
	 * @return {@code true} if the upload succeeds, {@code false} otherwise
	 */
	@PostMapping("/audit/uploadAttachement")
	public ResponseEntity<Boolean> uploadAttachmentFile(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("idProject") int idProject, 
			@RequestParam("idTopic") int idTopic, 
			@RequestParam("type") int type,
			@RequestParam("label") String label) throws ApplicationException {

		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		if (log.isDebugEnabled()) {
			log.debug(String.format("Uploading %s for project/audit : %d/%d of type %s", filename, idProject, idTopic, type));
		}

		FileType typeOfApplication = FileType.valueOf(type);
		
		AuditTopic auditProject = projectAuditHandler.getTopic(idProject, idTopic);
		
		storageService.store(file, projectAuditHandler.buildAttachmentFileName(idProject, idTopic, filename));

		if ((label == null) || (label.length() == 0)) {
			label = filename;
		}
		projectAuditHandler.updateAttachmentFile(
			idProject, 
			idTopic, 
			new AttachmentFile(auditProject.getAttachmentList().size(), filename, typeOfApplication, label));
	
		return new ResponseEntity<>(true, headers(), HttpStatus.OK);
	}
	
	/**
	 * Download an attachment file from the audit. 
	 * @param id the staff identifier
	 * @param request type type of request
	 * @return the file resource
	 */
	@GetMapping(value = "/audit/attachmentFile/{idProject}/{idTopic}/{idFile}")
	public ResponseEntity<Resource> downloadAttachmentFile(
			@PathVariable("idProject") int idProject, 
			@PathVariable("idTopic") int idTopic, 
			@PathVariable("idFile") int idFile, 
			HttpServletRequest request) throws ApplicationException {

		final AuditTopic auditTopic = projectAuditHandler.getTopic(idProject, idTopic);

		AttachmentFile attachment = auditTopic.getAttachmentList().get(idFile);
		if (attachment == null) {
			throw new ApplicationException (
					CODE_CANNOT_RETRIEVE_ATTACHMENTFILE,
					MessageFormat.format(LIB_CANNOT_RETRIEVE_ATTACHMENTFILE, idProject, idTopic, idFile));
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("Downloading file %s for the project/topic %d/%d", attachment.getFileName(), idProject, idTopic));
		}
		
		//
		// Load file as a Resource
		//
		Resource resource = storageService.loadAsResource(projectAuditHandler.buildAttachmentFileName(idProject, idTopic, attachment.getFileName()));

		String contentType = storageService.getContentType(attachment.getTypeOfFile());

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
			.body(resource);
			
	}
	
}