import { ComponentFixture, TestBed, TestModuleMetadata, tick, fakeAsync, waitForAsync } from '@angular/core/testing';

import { SkillComponent } from './skill.component';
import { InitTest } from '../test/init-test';
import { of } from 'rxjs';
import { SkillService } from './service/skill.service';
import { ListSkillService } from './list-skill-service/list-skill.service';
import { Skill } from '../data/skill';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { DetectionTemplate } from '../data/detection-template';

describe('SkillComponent', () => {
	let component: SkillComponent;
	let fixture: ComponentFixture<SkillComponent>;
	let spyGetSkills;
	let getSkill;
	let detectionTemplates$;
	let debugElement: DebugElement;
	let skill: Skill;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [SkillComponent],
			providers: [       {
				provide: ActivatedRoute, useValue: {
					params: of({id: 1})
				},
			} ],
			imports: [ HttpClientTestingModule]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

		fixture = TestBed.createComponent(SkillComponent);
		component = fixture.componentInstance;

		const listSkillService = TestBed.inject(ListSkillService);
		const skillService = TestBed.inject(SkillService);
		skill = new Skill(1, 'First skill');
		const skills = [];
		skills.push(skill);
		spyGetSkills = spyOn(listSkillService, 'getSkills').and.returnValue(skills);
		getSkill = spyOn(listSkillService, 'getSkill$').and.returnValue(of(skill));
		detectionTemplates$ = spyOn(skillService, 'detectionTemplates$')
			.and.returnValue(of([
				{
					detectionType: 0,
					pattern: 'test for 0'
				},
				{
					detectionType: 1,
					pattern: 'test for 1'
				}

			]));

	}));

	beforeEach(() => {


	});

	it('Testing the SkillForm handling template & pattern', fakeAsync(() => {
		fixture = TestBed.createComponent(SkillComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement;
		skill.detectionTemplate = new DetectionTemplate(0, 'my personal pattern');
		fixture.detectChanges();

		expect(component).toBeTruthy();
		tick();

		expect(getSkill).toHaveBeenCalled();
		expect(detectionTemplates$).toHaveBeenCalled();

		expect(1).toEqual(component.skill.id);
		expect('First skill').toEqual(component.skill.title);
		expect('First skill').toEqual(component.profileSkill.get('title').value);

		const select = debugElement.query(By.css('select')).nativeElement;
		expect(select).toBeDefined();
		// The FIRST line in the option is not selected
		let option = fixture.debugElement.queryAll(By.css('option'))[1];
		expect(option).toBeDefined();
		expect(option.nativeElement.selected).toBe(true);

		// The second line in the option is not selected
		option = fixture.debugElement.queryAll(By.css('option'))[2];
		expect(option).toBeDefined();
		expect(option.nativeElement.selected).toBe(false);

		const patternNative = debugElement.query(By.css('#pattern')).nativeElement;
		expect(patternNative).toBeDefined();
		expect('my personal pattern').toEqual(patternNative.value);
		expect('my personal pattern').toEqual(component.profileSkill.get('pattern').value);

	}));

	it('should manage the input/output of the  ', () => {
		fixture = TestBed.createComponent(SkillComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement;

	});

});
