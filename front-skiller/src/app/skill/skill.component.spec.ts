import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SkillComponent } from './skill.component';
import { RootTestModule } from '../root-test/root-test.module';

describe('SkillComponent', () => {
	let component: SkillComponent;
	let fixture: ComponentFixture<SkillComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [RootTestModule]
		})
		.compileComponents();
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
