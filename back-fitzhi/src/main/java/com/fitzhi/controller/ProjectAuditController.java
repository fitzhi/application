package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_CANNOT_RETRIEVE_ATTACHMENTFILE;
import static com.fitzhi.Error.LIB_CANNOT_RETRIEVE_ATTACHMENTFILE;
import static com.fitzhi.Global.BACKEND_RETURN_CODE;
import static com.fitzhi.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamAuditEntries;
import com.fitzhi.controller.in.BodyParamAuditEntry;
import com.fitzhi.controller.in.BodyParamProjectAttachmentFile;
import com.fitzhi.data.internal.AttachmentFile;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.TopicWeight;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.StorageService;

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
@RequestMapping("/api/project/audit")
public class ProjectAuditController {

	@Autowired
	ProjectAuditHandler projectAuditHandler;
	
	@Autowired
	ProjectHandler projectHandler;

	/**
	 * Storage service dedicated to upload/download the audit attachments.
	 */
	@Autowired
	@Qualifier("Attachment")
    StorageService storageService;
	
	@PostMapping(path="/saveTopic")
	public ResponseEntity<Boolean> saveTopic(@RequestBody BodyParamAuditEntry param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/addTopic for project.id %d and topic.id %d", 
						param.getIdProject(), param.getAuditTopic().getIdTopic()));
		}
		
		try {
			projectAuditHandler.addTopic(param.getIdProject(), param.getAuditTopic().getIdTopic());
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	/**
	 * load and return an {@link AuditTopic}
	 * @param idProject the project identifier
	 * @param sonarKey the key of the Sonar project
	 * @return an HTTP response with the found Sonar project, or {@code null} if none is found
	 */
	@GetMapping(path="/loadTopic/{idProject}/{idTopic}")
	public ResponseEntity<AuditTopic> getTopicAudit(
			@PathVariable("idProject") int idProject,
			@PathVariable("idTopic") int idTopic) {

		HttpHeaders headers = new HttpHeaders();

		try {
			AuditTopic auditProject = projectAuditHandler.getTopic(idProject, idTopic);
			return  new ResponseEntity<>(auditProject, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@PostMapping(path="/removeTopic")
	public ResponseEntity<Boolean> removeTopic(@RequestBody BodyParamAuditEntry param) {
		
		HttpHeaders headers = new HttpHeaders();
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/removeTopic for project.id %d and topic.id %d", 
						param.getIdProject(), param.getAuditTopic().getIdTopic()));
		}
		
		try {
			projectAuditHandler.removeTopic(param.getIdProject(), param.getAuditTopic().getIdTopic(), false);
			projectAuditHandler.processAndSaveGlobalAuditEvaluation(param.getIdProject());			
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}

	@PostMapping(path="/saveEvaluation")
	public ResponseEntity<Boolean> saveEvaluation(@RequestBody BodyParamAuditEntry param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/saveEvaluation for project.id %d and topic.id %d", 
						param.getIdProject(), param.getAuditTopic().getIdTopic()));
		}
		
		try {
			projectAuditHandler.saveEvaluation(param.getIdProject(), param.getAuditTopic().getIdTopic(), param.getAuditTopic().getEvaluation());
			projectAuditHandler.processAndSaveGlobalAuditEvaluation(param.getIdProject());
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@PostMapping(path="/saveReport")
	public ResponseEntity<Boolean> saveReport(@RequestBody BodyParamAuditEntry param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/saveReport for project.id %d and topic.id %d", 
						param.getIdProject(), param.getAuditTopic().getIdTopic()));
		}
		
		try {
			final AuditTopic auditTopic = param.getAuditTopic();
			projectAuditHandler.saveReport(
					param.getIdProject(), auditTopic.getIdTopic(), auditTopic.getReport());
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}

	@PostMapping(path="/saveWeights")
	public ResponseEntity<Boolean> saveWeights(@RequestBody BodyParamAuditEntries param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/saveWeights for project.id %d", param.getIdProject()));
		}
		
		try {
			
			List<TopicWeight> weights = new ArrayList<>();
			for (AuditTopic auditTopic : param.getDataEnvelope()) {
				if (log.isDebugEnabled()) {
					log.debug(String.format(
						"Saving weight %d for %d", auditTopic.getWeight(), auditTopic.getIdTopic()));
				}
				weights.add(new TopicWeight(auditTopic.getIdTopic(), auditTopic.getWeight()));
			}
			
			projectAuditHandler.saveWeights(param.getIdProject(), weights);
			projectAuditHandler.processAndSaveGlobalAuditEvaluation(param.getIdProject());
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	
	@PostMapping(path="/saveAttachmentFile")
	public ResponseEntity<Boolean> saveAttachmentFile(@RequestBody BodyParamProjectAttachmentFile param) {

		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/saveAttachmentFile for project.id %d, topic.id %d", param.getIdProject(), param.getIdTopic()));
		}
		
		try {
			projectAuditHandler.updateAttachmentFile (param.getIdProject(), param.getIdTopic(), param.getAttachmentFile());
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@PostMapping(path="/removeAttachmentFile")
	public ResponseEntity<Boolean> removeAttachmentFile(@RequestBody BodyParamProjectAttachmentFile param) {

		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/removeAttachmentFile for project.id %d, topic.id %d, attachmentFile %d", 
					param.getIdProject(), param.getIdTopic(), param.getAttachmentFile().getIdFile()));
		}
		
		try {
			projectAuditHandler.removeAttachmentFile (param.getIdProject(), param.getIdTopic(), param.getAttachmentFile().getIdFile());
			
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	/**
	 * Upload an attachment to an audit topic
	 * @param file the attachment file
	 * @param idProject the project identifier
	 * @param idTopic the topic identifier
	 * @param type the type of file (WORD, PDF...)
	 * @return {@code true} if the upload succeeds, {@code false} otherwise
	 */
	@PostMapping("/uploadAttachement")
	public ResponseEntity<Boolean> uploadAttachmentFile(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("idProject") int idProject, 
			@RequestParam("idTopic") int idTopic, 
			@RequestParam("type") int type,
			@RequestParam("label") String label) {

		
		HttpHeaders headers = new HttpHeaders();
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		if (log.isDebugEnabled()) {
			log.debug(String.format("Uploading %s for project/audit : %d/%d of type %s", filename, idProject, idTopic, type));
		}

		FileType typeOfApplication = FileType.valueOf(type);
		
		try {
			AuditTopic auditProject = projectAuditHandler.getTopic(idProject, idTopic);
			
			storageService.store(file, projectAuditHandler.buildAttachmentFileName(idProject, idTopic, filename));

			if ((label == null) || (label.length() == 0)) {
				label = filename;
			}
			projectAuditHandler.updateAttachmentFile(
				idProject, 
				idTopic, 
				new AttachmentFile(auditProject.getAttachmentList().size(), filename, typeOfApplication, label));
		
			return new ResponseEntity<>(true, headers, HttpStatus.OK);
		} catch (SkillerException e) {
			return new ResponseEntity<>(
				false, 
				headers, 
				HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Download an attachment file from the audit. 
	 * @param id the staff identifier
	 * @param request type type of request
	 * @return the file resource
	 */
	@GetMapping(value = "/attachmentFile/{idProject}/{idTopic}/{idFile}")
	public ResponseEntity<Resource> downloadAttachmentFile(
		    @PathVariable("idProject") int idProject, 
		    @PathVariable("idTopic") int idTopic, 
		    @PathVariable("idFile") int idFile, 
		    HttpServletRequest request) {

		HttpHeaders headers = new HttpHeaders();

		try {
			final AuditTopic auditTopic = projectAuditHandler.getTopic(idProject, idTopic);

			AttachmentFile attachment = auditTopic.getAttachmentList().get(idFile);
			if (attachment == null) {
				throw new SkillerException (
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
	        
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
}