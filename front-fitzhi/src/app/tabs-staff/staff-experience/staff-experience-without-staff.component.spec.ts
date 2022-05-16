import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { ExpectedConditions } from 'protractor';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { take } from 'rxjs/operators';
import { Collaborator } from 'src/app/data/collaborator';
import { DeclaredExperience } from 'src/app/data/declared-experience';
import { Experience } from 'src/app/data/experience';
import { MessageService } from 'src/app/interaction/message/message.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { InitTest } from 'src/app/test/init-test';
import { StaffService } from '../service/staff.service';
import { StaffExperienceComponent } from './staff-experience.component';
import { TagifyEditableState } from './tagify-stars/tagify-editable-state';
import { TagifyStarsComponent } from './tagify-stars/tagify-stars.component';


describe('StaffExperienceComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let skillService: SkillService;
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


	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, StaffExperienceComponent, TagifyStarsComponent],
			providers: [SkillService, StaffService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		skillService = TestBed.inject(SkillService);
		staffService = TestBed.inject(StaffService);
		tabsStaffListService = TestBed.inject(TabsStaffListService);
		messageService = TestBed.inject(MessageService);

		staffService.collaborator = new Collaborator();
		staffService.collaborator.idStaff = -1;
		staffService.collaborator.experiences = [];
		staffService.collaboratorLoaded$.next(true);

		skillService.allSkills = [
				{
					id: 1,
					title: 'Java'
				},
				{
					id: 2,
					title: 'Javascript'
				}
		];
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		fixture.detectChanges();
	});

	it('should be created successfully', () => {
		expect(component).toBeTruthy();
	});

	it('should NOT be able to update any experience, if there is no edited collaborator.', () => {
		expect(field('#image_upLoadCV')).toBeNull();
	});

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

});
