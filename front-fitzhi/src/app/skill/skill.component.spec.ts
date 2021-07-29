import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, TestModuleMetadata, tick, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { InitTest } from '../test/init-test';
import { SkillService } from './service/skill.service';
import { SkillComponent } from './skill.component';


describe('SkillComponent', () => {
	let component: SkillComponent;
	let fixture: ComponentFixture<SkillComponent>;
	let debugElement: DebugElement;

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
