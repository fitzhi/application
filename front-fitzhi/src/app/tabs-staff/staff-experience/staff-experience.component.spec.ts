import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { InitTest } from 'src/app/test/init-test';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Collaborator } from 'src/app/data/collaborator';
import { StaffExperienceComponent } from './staff-experience.component';
import { Component } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffService } from '../service/staff.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { Skill } from 'src/app/data/skill';
import { TagifyStarsComponent } from './tagify-stars/tagify-stars.component';

describe('StaffExperienceComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let skillService: SkillService;
	let staffDataExchangeService: StaffDataExchangeService;

	@Component({
		selector: 'app-host-component',
		template: `	<div>
						<app-staff-experience [selectedTab$]="selectedTab$"></app-staff-experience>
					</div>`
	})
	class TestHostComponent {
		public selectedTab$ = new BehaviorSubject<number>(1);
	}


	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, StaffExperienceComponent, TagifyStarsComponent],
			providers: [SkillService, StaffDataExchangeService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
}));

	beforeEach(() => {
		staffDataExchangeService = TestBed.inject(StaffDataExchangeService);
		skillService = TestBed.inject(SkillService);
		
		staffDataExchangeService.collaborator = new Collaborator();
		staffDataExchangeService.collaborator.idStaff = 1789;
		staffDataExchangeService.collaborator.firstName = 'Zinedine';
		staffDataExchangeService.collaborator.lastName = 'Zidane';
		staffDataExchangeService.collaborator.typeOfApplication = null;
		staffDataExchangeService.collaborator.experiences = []; 
		staffDataExchangeService.collaboratorLoaded$.next(true);
		
		skillService.allSkills= [
				{
					id: 1,
					title: 'Java'
				}

		]
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
