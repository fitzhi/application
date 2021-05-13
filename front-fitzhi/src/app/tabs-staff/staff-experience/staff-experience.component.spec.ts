import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { InitTest } from 'src/app/test/init-test';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Collaborator } from 'src/app/data/collaborator';
import { StaffExperienceComponent } from './staff-experience.component';
import { Component, ViewChild } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffService } from '../service/staff.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { Skill } from 'src/app/data/skill';
import { TagifyStarsComponent } from './tagify-stars/tagify-stars.component';
import { By } from '@angular/platform-browser';
import { table } from 'console';
import { Experience } from 'src/app/data/experience';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { MessageService } from 'src/app/interaction/message/message.service';
import { DeclaredExperience } from 'src/app/data/declared-experience';

describe('StaffExperienceComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let skillService: SkillService;
	let staffDataExchangeService: StaffDataExchangeService;
	let staffService: StaffService;
	let tabsStaffListService: TabsStaffListService;
	let messageService: MessageService;

	@Component({
		selector: 'app-host-component',
		template: `	<div>
						<app-staff-experience [selectedTab$]="selectedTab$"></app-staff-experience>
					</div>`
	})
	class TestHostComponent {
		public selectedTab$ = new BehaviorSubject<number>(1);
		@ViewChild(StaffExperienceComponent) staffExperienceComponent: StaffExperienceComponent;
	}


	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, StaffExperienceComponent, TagifyStarsComponent],
			providers: [SkillService, StaffService, StaffDataExchangeService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
}));

	beforeEach(() => {
		staffDataExchangeService = TestBed.inject(StaffDataExchangeService);
		skillService = TestBed.inject(SkillService);
		staffService = TestBed.inject(StaffService);
		tabsStaffListService = TestBed.inject(TabsStaffListService);
		messageService = TestBed.inject(MessageService);

		staffDataExchangeService.collaborator = new Collaborator();
		staffDataExchangeService.collaborator.idStaff = 1789;
		staffDataExchangeService.collaborator.firstName = 'Zinedine';
		staffDataExchangeService.collaborator.lastName = 'Zidane';
		staffDataExchangeService.collaborator.active = true;
		staffDataExchangeService.collaborator.typeOfApplication = null;
		staffDataExchangeService.collaborator.experiences = [];
		staffDataExchangeService.collaborator.experiences.push(new Experience(1, 'Java', 3));
		staffDataExchangeService.collaboratorLoaded$.next(true);
		
		skillService.allSkills= [
				{
					id: 1,
					title: 'Java'
				},
				{
					id: 2,
					title: 'Javascript'
				}
		]
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		fixture.detectChanges();
	});

	it('should be created successfully', () => {
		expect(component).toBeTruthy();
	});

	it('should UPDATE correctly the experience of a collaborator.', () => {
		expect(component).toBeTruthy();
		fixture.detectChanges();

		const spyUpdateExperience$ = spyOn(staffService, 'updateExperience$').and.returnValue(of(true));
		const spyActualizeCollaborator = spyOn(tabsStaffListService, 'actualizeCollaborator').and.returnValue(null);
		const spyMessageService = spyOn(messageService, 'success').and.returnValue(null);

		component.staffExperienceComponent.updateExperience(1, 1);
		fixture.detectChanges();

		expect(spyUpdateExperience$).toHaveBeenCalled();
		expect(spyMessageService).toHaveBeenCalled();
		expect(spyActualizeCollaborator).toHaveBeenCalled();
	});


	it('should ADD correctly the experience of a collaborator.', () => {
		expect(component).toBeTruthy();
		fixture.detectChanges();

		const spyAddExperience$ = spyOn(staffService, 'addExperience$').and.returnValue(of(true));
		const spyActualizeCollaborator = spyOn(tabsStaffListService, 'actualizeCollaborator').and.returnValue(null);
		const spyMessageService = spyOn(messageService, 'success').and.returnValue(null);

		component.staffExperienceComponent.addExperience(
			2, 'Javascript',
			4);
		fixture.detectChanges();

		expect(spyAddExperience$).toHaveBeenCalled();
		expect(spyMessageService).toHaveBeenCalled();
		expect(spyActualizeCollaborator).toHaveBeenCalled();

		// We have added the javascript skill to Zidane.
		expect(staffDataExchangeService.collaborator.experiences.length).toBe(2);
	});

	it('should handle correctly a failure when ADDING an experience to a collaborator.', () => {
		expect(component).toBeTruthy();
		fixture.detectChanges();

		const spyAddExperience$ = spyOn(staffService, 'addExperience$')
			.and
			.returnValue(throwError(new Error('{ "status": 400, "message": "oops!" }' )));
		const spyActualizeCollaborator = spyOn(tabsStaffListService, 'actualizeCollaborator')
			.and.returnValue(null);
		const spyMessageSuccess = spyOn(messageService, 'success').and.returnValue(null);
		const spyMessageError = spyOn(messageService, 'error').and.returnValue(null);

		component.staffExperienceComponent.addExperience(
			2, 'Javascript',
			4);
		fixture.detectChanges();

		expect(spyAddExperience$).toHaveBeenCalled();
		expect(spyMessageSuccess).not.toHaveBeenCalled();
		expect(spyMessageError).toHaveBeenCalled();
		expect(spyActualizeCollaborator).not.toHaveBeenCalled();

		// We have added the javascript skill to Zidane.
		expect(staffDataExchangeService.collaborator.experiences.length).toBe(1);
	});

	it('should REMOVE correctly the experience of a collaborator.', () => {
		expect(component).toBeTruthy();
		fixture.detectChanges();

		const spyRemoveExperience$ = spyOn(staffService, 'removeExperience$').and.returnValue(of(true));
		const spyMessageService = spyOn(messageService, 'success').and.returnValue(null);
		const spyActualizeCollaborator = spyOn(tabsStaffListService, 'actualizeCollaborator').and.returnValue(null);

		component.staffExperienceComponent.removeExperience(2, 'Javascript');
		fixture.detectChanges();

		expect(spyRemoveExperience$).toHaveBeenCalled();
		expect(spyMessageService).toHaveBeenCalled();
		expect(spyActualizeCollaborator).toHaveBeenCalled();

		// We have removed "Java" from the collaborator experiences.
		expect(staffDataExchangeService.collaborator.experiences.length).toBe(0);
	});

	it('should UPDATE the server with the new skills retrieved by the application file.', () => {
		expect(component).toBeTruthy();

		const newExperiences: DeclaredExperience[] = [
			{
				idSkill: 1000,
				title: "One thousand",
				times: 1000
				
			},
			{
				idSkill: 100,
				title: "One hundred",
				times: 100
				
			}
		];
		const spyAddDeclaredExperience$ = spyOn(staffService, 'setDeclaredExperience$')
			.and.returnValue(of(staff()));

		component.staffExperienceComponent.updateStaffWithNewExperiences(1789, newExperiences);

		expect(spyAddDeclaredExperience$).toHaveBeenCalled();

	});

	it('should NOT CALL the server with no new skill has been retrieved in the application file.', () => {
		const spyAddDeclaredExperience$ = spyOn(staffService, 'setDeclaredExperience$');
		component.staffExperienceComponent.updateStaffWithNewExperiences(1789, []);
		expect(spyAddDeclaredExperience$).not.toHaveBeenCalled();
	});

	it('should ISOLATE the new skills detected from the actual declared.', () => {

		const experiences: DeclaredExperience[] = [
			{
				idSkill: 1000,
				title: "Brand new",
				times: 1000
				
			},
			{
				idSkill: 1001,
				title: "One another new",
				times: 100
				
			},
			{
				idSkill: 1002,
				title: "Old one",
				times: 100
				
			}
		];

		component.staffExperienceComponent.staff = staff();
		component.staffExperienceComponent.staff.experiences.push(new Experience(1002, 'Old one', 10));
		const result = component.staffExperienceComponent.isolateNewExperiences(experiences);
		expect(result.length).toBe(2);
		expect(result[0].idSkill).toBe(1000);
		expect(result[1].idSkill).toBe(1001);
	});

	function staff(): Collaborator {
		const staff = new Collaborator();
		staff.idStaff = 1789;
		staff.experiences = [];
		return staff;
	}
});
