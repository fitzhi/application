import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, TestModuleMetadata, tick, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import { InitTest } from '../test/init-test';
import { SkillService } from './service/skill.service';
import { SkillComponent } from './skill.component';


describe('SkillComponent', () => {
	let component: SkillComponent;
	let fixture: ComponentFixture<SkillComponent>;
	let debugElement: DebugElement;
	let skillService: SkillService;

	beforeEach(waitForAsync(() => {
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

		skillService = TestBed.inject(SkillService);
		skillService.detectionTemplatesLoaded$.next(true);
		fixture.detectChanges();
	}));

	beforeEach(() => {
		spyOn(skillService, 'detectionTemplates$')
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
		fixture.detectChanges();
	});

	it('should activate the Ok button when the skill is defined ONLY by its title.', fakeAsync(() => {
		debugElement = fixture.debugElement;
		const buttonOk = debugElement.query(By.css('#buttonOk'));
		const nativeButtonOk = buttonOk.nativeElement;
		expect(nativeButtonOk.disabled).toBeTruthy();

		sendInput('#title', 'Incredible kill');
		expect(nativeButtonOk.disabled).toBeFalsy();
		expect(component.profileSkill.valid).toBeTruthy();
	}));

	it('should activate the Ok button when the skill is defined and the couple (detectionType, pattern) is defined as well.', fakeAsync(() => {
		debugElement = fixture.debugElement;
		const buttonOk = debugElement.query(By.css('#buttonOk'));
		const nativeButtonOk = buttonOk.nativeElement;
		expect(nativeButtonOk.disabled).toBeTruthy();

		sendInput('#title', 'Another incredible skill');

		// We select a detection type. (not enough to validate the form)
		const select = debugElement.query(By.css('#detectionType')).nativeElement;
		expect(select).toBeDefined();
		select.value = select.options[2].value;
		select.dispatchEvent(new Event('change'));
		fixture.detectChanges();
		expect(nativeButtonOk.disabled).toBeTruthy();
		expect(component.profileSkill.valid).toBeFalsy();

		// The couple (detectionType, pattern) is complete. Now, we can activate the button.
		sendInput('#pattern', 'A possible pattern');
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
