import { async, ComponentFixture, TestBed, TestModuleMetadata, tick, fakeAsync } from '@angular/core/testing';

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
	let debugElement: DebugElement;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [SkillComponent],
			providers: [],
			imports: [ HttpClientTestingModule]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

		fixture = TestBed.createComponent(SkillComponent);
		component = fixture.componentInstance;

		const skillService = TestBed.inject(SkillService);
		skillService.detectionTemplatesLoaded$.next(true);
		fixture.detectChanges();
	}));

	beforeEach(() => {
	});

	it('should manage correctly the input/output in its form', fakeAsync(() => {
		debugElement = fixture.debugElement;
		const buttonOk = debugElement.query(By.css('#buttonOk'));
		const nativeButtonOk = buttonOk.nativeElement;
		expect(nativeButtonOk.disabled).toBeTruthy();

		sendInput('#title', 'Incredible kill');
		expect(nativeButtonOk.disabled).toBeFalsy();
		expect(component.profileSkill.valid).toBeTruthy();
		
	}));
	
	function sendInput(id: string, text: string) {
		const input = debugElement.query(By.css(id)).nativeElement;
		input.value = text;
		input.dispatchEvent(new Event('input'));
		tick();
		fixture.detectChanges();
		return fixture.whenStable();
	  }
});
