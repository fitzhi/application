import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { SkillComponent } from './skill.component';
import { InitTest } from '../test/init-test';
import { RouterModule } from '@angular/router';

describe('SkillComponent', () => {
	let component: SkillComponent;
	let fixture: ComponentFixture<SkillComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [SkillComponent],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(SkillComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
