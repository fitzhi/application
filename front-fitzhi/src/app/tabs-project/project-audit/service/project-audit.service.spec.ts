import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { of } from 'rxjs';
import { AuditDetail } from 'src/app/data/audit-detail';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { Project } from 'src/app/data/project';
import { MessageService } from 'src/app/interaction/message/message.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { AuditChosenDetail } from '../project-audit-badges/audit-badge/audit-chosen-detail';
import { TopicWeight } from '../project-audit-badges/topic-weight';
import { Topic } from '../table-categories/topic';
import { ProjectAuditService } from './project-audit.service';


describe('ProjectAuditService', () => {
	let service: ProjectAuditService;
	let projectService: ProjectService;
	let messageService: MessageService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			declarations: [],
				providers: [ProjectService, ReferentialService, CinematicService],
				imports: [HttpClientTestingModule, MatDialogModule]
		});
		service = TestBed.inject(ProjectAuditService);
		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'The revolutionary project');
		projectService.project.audit = { 
			"3": new AuditTopic(1789, 3, 80, 30),
			"5": new AuditTopic(1789, 5, 50, 70) 
		}
		messageService = TestBed.inject(MessageService);
	});

	it('should be created successfully.', () => {
		expect(service).toBeTruthy();
	});

	it('should initialize the auditTopics[] array successfully.', () => {
		expect(service).toBeTruthy();
		service.initializeAuditTopic({'0': 'Unused', '3': 'The third', '5': '5 of Chanel'});
		expect(service.auditTopics.length).toBe(2);
		expect(service.auditTopics[0].idProject).toBe(1789);
		expect(service.auditTopics[0].title).toBe('The third');
	});

	it('should share correctly the weights between the topics.', () => {
		projectService.project.audit[7] = new AuditTopic(1789, 7, 0, 70) 
		service.initializeAuditTopic({'0': 'Unused', '3': 'The third', '5': '5 of Chanel', '7': 'The magnificent seven'});
		expect(service.auditTopics.length).toBe(3);

		service.assignWeights();
		expect(service.auditTopics[0].idTopic).toBe(3);
		expect(service.auditTopics[0].weight).toBe(33);
		expect(service.auditTopics[1].idTopic).toBe(5);
		expect(service.auditTopics[1].weight).toBe(33);
		expect(service.auditTopics[2].idTopic).toBe(7);
		expect(service.auditTopics[2].weight).toBe(34);

	});

	it('should transfert the updated weights to the project.', () => {
		projectService.project.audit[7] = new AuditTopic(1789, 7, 0, 70) 
		service.initializeAuditTopic({'0': 'Unused', '3': 'The third', '5': '5 of Chanel', '7': 'The magnificent seven'});
		expect(service.auditTopics.length).toBe(3);
		service.assignWeights();

		service.impactWeightsInProject();

		expect(projectService.project.audit[3].weight).toBe(33);
		expect(projectService.project.audit[5].weight).toBe(33);
		expect(projectService.project.audit[7].weight).toBe(34);
	});

	it('should update the topics when a user has ADDED a topic from the audit.', done => {

		projectService.projectLoaded$.subscribe({
			next: doneAndOk => done()
		});
		const spySaveAuditTopicWeights = spyOn(projectService, 'saveAuditTopicWeights$')
			.and.returnValue(of(true));
		const spyProcessGlobalAuditEvaluation = spyOn(projectService, 'processGlobalAuditEvaluation')
			.and.returnValue(null);
		const spyAssignWeights = spyOn(service, 'assignWeights').and.returnValue(null);
		const spyImpactWeightsInProject = spyOn(service, 'impactWeightsInProject').and.returnValue(null);

		const topic = new Topic(true, 7, 'Seven');
		service.initializeAuditTopic({'0': 'Unused', '3': 'The third', '5': '5 of Chanel', '7': 'The magnificent seven!'});
		service.onCategoryUpdated(topic);
		expect(service.auditTopics.length).toBe(3);
		expect(service.auditTopics[2]).toBeDefined();
		expect(service.auditTopics[2].idTopic).toBe(7);

		expect(spyAssignWeights).toHaveBeenCalled();
		expect(spyImpactWeightsInProject).toHaveBeenCalled();
		expect(spySaveAuditTopicWeights).toHaveBeenCalled();
		expect(spyProcessGlobalAuditEvaluation).toHaveBeenCalled();
	});

	it('should update the topics when a user has REMOVED a topic from the audit.', done => {

		projectService.projectLoaded$.subscribe({
			next: doneAndOk => done()
		});
		const spySaveAuditTopicWeights = spyOn(projectService, 'saveAuditTopicWeights$')
			.and.returnValue(of(true));
		const spyProcessGlobalAuditEvaluation = spyOn(projectService, 'processGlobalAuditEvaluation')
			.and.returnValue(null);
		const spyAssignWeights = spyOn(service, 'assignWeights').and.returnValue(null);
		const spyImpactWeightsInProject = spyOn(service, 'impactWeightsInProject').and.returnValue(null);

		const topic = new Topic(false, 3, 'The third');
		service.initializeAuditTopic({'0': 'Unused', '3': 'The third', '5': '5 of Chanel', '7': 'The magnificent seven!'});
		service.onCategoryUpdated(topic);
		expect(service.auditTopics.length).toBe(1);
		expect(service.auditTopics[0]).toBeDefined();
		expect(service.auditTopics[0].idTopic).toBe(5);

		expect(spyAssignWeights).toHaveBeenCalled();
		expect(spyImpactWeightsInProject).toHaveBeenCalled();
		expect(spySaveAuditTopicWeights).toHaveBeenCalled();
		expect(spyProcessGlobalAuditEvaluation).toHaveBeenCalled();
	});

	it('should remove correctly all panel related to a topic, if a topic is removed from the audit.', done => {
		const acd = [];
		acd.push(new AuditChosenDetail(77, AuditDetail.Report));
		acd.push(new AuditChosenDetail(77, AuditDetail.Tasks));
		acd.push(new AuditChosenDetail(77, null));
		acd.push(new AuditChosenDetail(3, AuditDetail.Report));
		acd.push(new AuditChosenDetail(3, AuditDetail.Tasks));
		acd.push(new AuditChosenDetail(3, null));

		const spy = spyOn(service, 'removeSecondaryDetailsPanel').and.callThrough();
		service.removeSecondaryDetailsPanel(77, acd);
		expect (acd.length).toBe(3);

		expect(service.removeSecondaryDetailsPanel).toHaveBeenCalledTimes(4);

		done();
	});

	it('should warn the user if the sum of weights is not equal to 100.', () => {
		service.initializeAuditTopic({'0': 'Unused', '3': 'The third', '5': '5 of Chanel', '7': 'The magnificent seven!'});
		const spyWarningMessage = spyOn(messageService, 'warning').and.returnValue(null);
		const tp = new TopicWeight(3, 10, 2);
		service.onWeightChange(tp);
		expect (spyWarningMessage).toHaveBeenCalled();
	});

	it('should display a success message if the sum of weights is correct.', () => {
		service.initializeAuditTopic({'0': 'Unused', '3': 'The third', '5': '5 of Chanel', '7': 'The magnificent seven!'});
		const spySuccessMessage = spyOn(messageService, 'success').and.returnValue(null);
		const spySaveAuditTopicWeights = spyOn(projectService, 'saveAuditTopicWeights$').and.returnValue(of(true));
		const tp = new TopicWeight(3, 30, 2);
		service.onWeightChange(tp);
		expect (spySuccessMessage).toHaveBeenCalled();
	});

});
