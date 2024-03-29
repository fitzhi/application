import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { InitTest } from '../test/init-test';
import { SkillComponent } from './skill.component';


describe('SkillComponent', () => {
	let component: SkillComponent;
	let fixture: ComponentFixture<SkillComponent>;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [SkillComponent],
			providers: [       {
				provide: ActivatedRoute, useValue: {
					params: of({id: undefined})
				},
			} ],
			imports: [ HttpClientTestingModule]
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

	it('should be create empty without id', () => {
		expect(component).toBeTruthy();
	});

});
