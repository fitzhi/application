import { async, ComponentFixture, TestBed, TestModuleMetadata, tick, fakeAsync } from '@angular/core/testing';

import { SkillComponent } from './skill.component';
import { InitTest } from '../test/init-test';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Observable, of } from 'rxjs';
import { SkillService } from './service/skill.service';
import { ListSkillService } from './list-skill-service/list-skill.service';
import { Skill } from '../data/skill';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from '../service/referential.service';
import { HttpClientModule } from '@angular/common/http';

describe('SkillComponent', () => {
	let component: SkillComponent;
	let fixture: ComponentFixture<SkillComponent>;

	beforeEach(async(() => {
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
